package com.politrons.kafka;


import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;

import java.util.Properties;
import java.util.stream.IntStream;

import static com.politrons.kafka.KafkaBalancing.ThrottlingKafkaConsumer.*;
import static java.time.Duration.ofSeconds;

/**
 * Here we cover the pattern of how when we have multiple topics for one consumer is doing round-robin balancing.
 */
@EmbeddedKafka(partitions = 4)
public class KafkaBalancing {

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, true, 4, "High", "Medium");

    private final EmbeddedKafkaBroker embeddedKafkaBroker = embeddedKafkaRule.getEmbeddedKafka();

    /**
     * In this example we create one instance of Kafka consumer with two topics,
     * and we publish in parallel in both topics
     */
    @Test
    public void specificPartition() throws InterruptedException {
        String broker = embeddedKafkaBroker.getBrokersAsString();
        ThrottlingKafkaConsumer consumer = new ThrottlingKafkaConsumer(
                broker,
                List.of("High", "Medium"),
                "groupId");
        consumer.start();
        Thread.sleep(5000);

        ThrottlingKafkaProducer producer = new ThrottlingKafkaProducer(broker, "producerId");

        Future.run(() -> {
            IntStream.range(0, 100).forEach(i -> {
                producer.publishMessage("key", ("hello world " + i).getBytes(), "High");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });
        });
        Future.run(() -> {
            IntStream.range(0, 100).forEach(i -> {
                producer.publishMessage("key", ("hello world " + i).getBytes(), "Medium");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
        Thread.sleep(60000);
    }

    static public class ThrottlingKafkaConsumer {

        public final String broker;
        public final List<String> topic;
        public final String groupId;
        public Consumer<String, byte[]> consumer;

        public ThrottlingKafkaConsumer(
                String broker,
                List<String> topic,
                String groupId) {
            this.broker = broker;
            this.topic = topic;
            this.groupId = groupId;
        }

        public void start() {
            this.consumer = createConsumer();
            Future.run(() -> consumeRecords(consumer));
        }

        private Consumer<String, byte[]> createConsumer() {
            Properties props = new Properties();
            props.put("bootstrap.servers", broker);
            props.put("group.id", groupId);
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
            final KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
            consumer.subscribe(topic.asJava());
            return consumer;
        }

        public void consumeRecords(final Consumer<String, byte[]> consumer) {
            while (true) {
                ConsumerRecords<String, byte[]> consumerRecords = consumer.poll(ofSeconds(5));
                consumerRecords.forEach(record -> System.out.printf("############ Consumer topic %s message %s ############\n", record.topic(), new String(record.value())));
                consumer.commitAsync();
            }
        }

        public static class ThrottlingKafkaProducer {

            public final String broker;
            public final String producerId;
            private final Producer<String, byte[]> producer;

            public ThrottlingKafkaProducer(String broker, String producerId) throws IllegalArgumentException {
                this.broker = broker;
                this.producerId = producerId;
                this.producer = new KafkaProducer<>(this.getProperties());
            }

            public Try<String> publishMessage(
                    String key,
                    byte[] payload,
                    String topic
            ) {
                return Try.of(() -> {
                    ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, payload);
                    this.producer.send(record).get();
                    return "Message sent to topic " + topic;
                });
            }

            private Properties getProperties() {
                Properties props = new Properties();
                props.put("bootstrap.servers", this.broker);
                props.put("client.id", this.producerId);
                props.put("timeout.ms", "10000");
                props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
                props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
                return props;
            }

        }
    }
}
