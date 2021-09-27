package curator;

import io.helidon.common.reactive.Multi;
import io.helidon.messaging.Channel;
import io.helidon.messaging.Messaging;
import io.helidon.messaging.connectors.kafka.KafkaConfigBuilder;
import io.helidon.messaging.connectors.kafka.KafkaConnector;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.retry.RetryNTimes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class ApacheCuratorFeature {

    private final String kafkaServer = "localhost:9092";
    private final String topic = "helidon_topic";
    private String path = "/counters/" + UUID.randomUUID();

    @Test
    public void curatorLockAndCounterFeature() throws Exception {

        //Broker one
        lockAndCounter();
        //Broker two
        lockAndCounter();

        kafkaConsumer();
        Thread.sleep(5000);
    }

    private void lockAndCounter() {
        CompletableFuture.supplyAsync(() -> {
                    int sleepMsBetweenRetries = 100;
                    int maxRetries = 3;
                    RetryPolicy retryPolicy = new RetryNTimes(
                            maxRetries, sleepMsBetweenRetries);

                    CuratorFramework client = CuratorFrameworkFactory
                            .newClient("127.0.0.1:2181", retryPolicy);
                    client.start();
                    try {
                        SharedCount counter = new SharedCount(client, path, 0);
                        counter.start();
                        InterProcessSemaphoreMutex sharedLock = new InterProcessSemaphoreMutex(client, path);
                        sharedLock.acquire();
                        if (counter.getCount() == 0) {
                            System.out.println("######## Sending event just once ##########");
                            counter.setCount(counter.getCount() + 1);
                            kafkaPublisher();
                        } else {
                            System.out.println("######## Event already sent ##########");
                        }
                        sharedLock.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return Void.class;
                }, Executors.newSingleThreadExecutor()
        );
    }

    private void kafkaConsumer() {
        Channel<String> consumerChannel = Channel.<String>builder()
                .name("kafka-connector")
                .publisherConfig(KafkaConnector.configBuilder()
                        .bootstrapServers(kafkaServer)
                        .groupId("helidon-group")
                        .topic(topic)
                        .autoOffsetReset(KafkaConfigBuilder.AutoOffsetReset.LATEST)
                        .enableAutoCommit(true)
                        .keyDeserializer(StringDeserializer.class)
                        .valueDeserializer(StringDeserializer.class)
                        .build()
                )
                .build();

        Messaging.builder()
                .connector(KafkaConnector.create())
                .listener(consumerChannel, payload -> {
                    System.out.println("Kafka says: " + payload);
                })
                .build()
                .start();
    }

    private void kafkaPublisher() {

        Channel<String> publisherChannel = Channel.<String>builder()
                .subscriberConfig(KafkaConnector.configBuilder()
                        .bootstrapServers(kafkaServer)
                        .topic(topic)
                        .keySerializer(StringSerializer.class)
                        .valueSerializer(StringSerializer.class)
                        .build()
                ).build();

        Messaging.builder()
                .connector(KafkaConnector.create())
                .publisher(publisherChannel,
                        Multi.just("hello", "kafka", "world", "helidon", "connector")
                                .map(Message::of))
                .build()
                .start();
    }
}
