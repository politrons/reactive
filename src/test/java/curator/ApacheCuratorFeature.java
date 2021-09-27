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

/**
 * IMPORTANT: In order to make this test works, we need to have a zookeeper running on port [2181]
 * I personally use docker image: docker pull johnnypark/kafka-zookeeper
 * And run with: docker run -p 2181:2181 -p 9092:9092 -e ADVERTISED_HOST=127.0.0.1  -e NUM_PARTITIONS=10 johnnypark/kafka-zookeeper
 *
 * Apache Curator is a DSL in top of Apache Zookeeper to allow connection management between znodes of the cluster
 * it also allows distributed lock and counter.
 * <p>
 * In this example we connect to Zookeeper two clients, and we check how we can lock the access to one particular logic,
 * and how we can share a counter between the members of the cluster
 */
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

    /**
     * For this test we run async each task to emulate a possible Kafka broker.
     * Once we create the client to communicate wth zooKeeper [CuratorFramework]
     *
     * First we create distributed counter [SharedCount] using the client and a specific path, which works just like a fileASystem (/counter/bla)
     * * Once we have the counter we can use [setCount(x)] to add a value into the counter or [getCount] to obtain the value.
     *
     * Then we crete a distributed lock [InterProcessSemaphoreMutex] using the client and a specific path.
     * * Once we have the sharedLock we can use [acquire] which it will block the next lines of code to be used by
     * * just one machine, and then we will release the lock with [release]
     */
    private void lockAndCounter() {
        CompletableFuture.supplyAsync(() -> {
                    CuratorFramework client = getCuratorFramework();
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
                        System.out.println(e);
                    }
                    return Void.class;
                }, Executors.newSingleThreadExecutor()
        );
    }

    /**
     * Here we create the [CuratorFramework] which behave like the client to communicate with Zookeeper to tell him what to do/
     * We use Retry policy to at least try 3 times with a backoff of 100ms between each time.
     */
    private CuratorFramework getCuratorFramework() {
        int sleepMsBetweenRetries = 100;
        int maxRetries = 3;
        RetryPolicy retryPolicy = new RetryNTimes(
                maxRetries, sleepMsBetweenRetries);

        CuratorFramework client = CuratorFrameworkFactory
                .newClient("127.0.0.1:2181", retryPolicy);
        client.start();
        return client;
    }

    /**
     * Consumer that it should only receive events from one publisher
     */
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

    /**
     * Publisher to send events. In this particular test we should invoke this publisher once.
     */
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
