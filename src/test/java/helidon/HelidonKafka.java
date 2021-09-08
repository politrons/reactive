package helidon;

import io.helidon.common.reactive.Multi;
import io.helidon.messaging.Channel;
import io.helidon.messaging.Messaging;
import io.helidon.messaging.connectors.kafka.KafkaConfigBuilder;
import io.helidon.messaging.connectors.kafka.KafkaConnector;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.Test;

public class HelidonKafka {

    private final String kafkaServer = "localhost:9092";
    private final String topic = "helidon_topic";

    @Test
    public void publisherConsumerKafka() throws InterruptedException {
        kafkaPublisher();
        kafkaConsumer();
        Thread.sleep(10000);
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
                .publisher(publisherChannel,
                        Multi.just("hello", "kafka", "world", "helidon", "connector")
                                .map(Message::of))
                .connector(KafkaConnector.create())
                .build()
                .start();
    }
}
