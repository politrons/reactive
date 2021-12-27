package com.politrons.kafka;

import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.internals.NoOpConsumerRebalanceListener;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.internals.Topic;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;

import java.util.Collection;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.IntStream;

import static io.vavr.API.*;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;
import static java.time.Duration.ofSeconds;

/**
 * Here we cover the pattern of how we can create multiple Kafka consumer under the same topic.
 * And reusing same partition.
 * First consumer subscribe to Kafka, and a partition is assigned. Then we unsubscribe the first consumer,
 * we subscribe the second consumer using [assign] which providing a [TopicPartition] we specify to which
 * [topic] and [partition] we can connect, so then we can continue consuming records, from the previous Consumer
 * left.
 */
@EmbeddedKafka(partitions = 2/*, brokerProperties = {"delete.retention.ms=100", "max.compaction.lag.ms=100", "cleanup.policy=compact"}*/)
public class KafkaLogCompaction {

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(2, true, 1, "Consumer-topic");

    private final EmbeddedKafkaBroker embeddedKafkaBroker = embeddedKafkaRule.getEmbeddedKafka();

    /**
     * In this example we create two instance of Kafka consumer.
     * One will consume 2 records and then we will unsubscribe
     * Second consumer it will assign to the partition of consumer1 and it will consume the rest of the records.
     */
    @Test
    public void specificPartition() throws InterruptedException {
        String broker = embeddedKafkaBroker.getBrokersAsString();
        String topic = "Consumer-topic";
        KafkaLogCompactedConsumer consumer1 = new KafkaLogCompactedConsumer(
                broker,
                topic,
                "groupId",
                "consumer1");
        consumer1.start(None());

        Thread.sleep(5000);

        KafkaLogCompactionProducer producer = new KafkaLogCompactionProducer(broker, "producerId");
        IntStream.range(0, 5).forEach(i -> producer.publishMessage(UUID.randomUUID().toString(), consumer1.partition, ("hello world " + i).getBytes(), topic));
        Thread.sleep(2000);
        consumer1.unsubscribe = true;
        Thread.sleep(2000);

        KafkaLogCompactedConsumer consumer2 = new KafkaLogCompactedConsumer(
                broker,
                topic,
                "groupId",
                "consumer2");
        consumer2.start(None());
        Thread.sleep(5000);
    }

    /**
     * Kafka consumer wrapper that receive in constructor arguments like topic, groupId, and
     * name to create a consumer, that it will consume in the partition assigned.
     */
    static public class KafkaLogCompactedConsumer {

        public final String broker;
        public final String topic;
        public final String groupId;
        public final String name;
        public Integer partition;
        public Consumer<String, byte[]> consumer;
        public Boolean unsubscribe = false;

        public KafkaLogCompactedConsumer(
                String broker,
                String topic,
                String groupId,
                String name) {
            this.broker = broker;
            this.topic = topic;
            this.groupId = groupId;
            this.name = name;
        }

        public void start(Option<Integer> maybePartition) {
            this.consumer = createConsumer(maybePartition);
            Future.run(() -> consumeRecords(consumer));
        }

        /**
         * We configure the Kafka connect and we subscribe to the Topic, adding also a Assignment listener class, to receive the partition
         * when the broker assign to us.
         */
        private Consumer<String, byte[]> createConsumer(Option<Integer> maybePartition) {
            Properties props = new Properties();
            props.put("bootstrap.servers", broker);
            props.put("group.id", groupId);
            props.put("enable.auto.commit", "false");
            props.put("auto.offset.reset", "earliest");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
            final KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);


            return Match(maybePartition).of(
                    Case($Some($()), partition -> {
                        this.partition = maybePartition.get();
                        consumer.assign(java.util.List.of(new TopicPartition(topic, maybePartition.get())));
                        return consumer;
                    }),
                    Case($None(), () -> {
                        consumer.subscribe(java.util.List.of(topic), partitionAssignmentListener());
                        return consumer;
                    })
            );
        }

        /**
         * Listener class that it will be invoked from Kafka consumer once an assigned or revoked partition happens
         */
        public NoOpConsumerRebalanceListener partitionAssignmentListener() {
            return new NoOpConsumerRebalanceListener() {
                @Override
                public void onPartitionsRevoked(final Collection<TopicPartition> collection) {
                    if (!collection.isEmpty()) {
                        var revokedPartition = List.ofAll(collection).head().partition();
                        System.out.printf("Partition Revoked %s\n", revokedPartition);
                    }
                }

                @Override
                public void onPartitionsAssigned(final Collection<TopicPartition> collection) {
                    if (!collection.isEmpty()) {
                        partition = partition != null ? List.ofAll(collection)
                                .find(tp -> tp.partition() == partition)
                                .getOrElse(List.ofAll(collection).head())
                                .partition() : List.ofAll(collection).head().partition();
                        System.out.printf("Partition Assigned %s\n", partition);
                    }
                }
            };
        }

        public void consumeRecords(final Consumer<String, byte[]> consumer) {
            KafkaLogCompactionProducer producer = new KafkaLogCompactionProducer(broker, "producerId");
            while (true) {
                ConsumerRecords<String, byte[]> consumerRecords = consumer.poll(ofSeconds(2));
                consumerRecords.forEach(record -> {
                    System.out.printf("############ Consumer %s partition %s message %s ############\n", name, partition, new String(record.value()));
                    producer.publishMessage(record.key(), record.partition(), null, record.topic());
                });
                if (unsubscribe) {
                    consumer.unsubscribe();
                }
            }
        }
    }

    /**
     * Kafka producer implementation.
     * We invoke [publishMessage] passing as argument the partition
     * where we want to send the message.
     */
    public static class KafkaLogCompactionProducer {

        public final String broker;
        public final String producerId;
        private final Producer<String, byte[]> producer;

        public KafkaLogCompactionProducer(String broker, String producerId) throws IllegalArgumentException {
            this.broker = broker;
            this.producerId = producerId;
            this.producer = new KafkaProducer<>(this.getProperties());
        }

        public Try<String> publishMessage(
                String key,
                Integer partition,
                byte[] payload,
                String topic
        ) {
            return Try.of(() -> {
                ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, partition, key, payload);
                this.producer.send(record).get();
                return "Message sent to partition " + partition;
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
