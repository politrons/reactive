package com.politrons.kafka;


import io.vavr.collection.List;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.internals.NoOpConsumerRebalanceListener;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;

import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.IntStream;

import static java.time.Duration.ofSeconds;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Here we cover the pattern of how we can create multiple Kafka consumer under the same topic.
 * Using different rate limit/interval configuration. So we can have multiple throttling consumers,
 * and once the broker assign a partition to each of them we can start sending messsge to them, to the
 * specific partition
 */
@EmbeddedKafka(partitions = 4)
public class KafkaThrottling {

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, true, 4, "Consumer-Throttling-topic");

    private final EmbeddedKafkaBroker embeddedKafkaBroker = embeddedKafkaRule.getEmbeddedKafka();

    /**
     * In this example we create two instance of Kafka consumer.
     * One will consume 2 records per second, and the other one
     * it will consume 5 records per 2 seconds.
     */
    @Test
    public void specificPartition() throws InterruptedException {
        String broker = embeddedKafkaBroker.getBrokersAsString();
        String topic = "Consumer-Throttling-topic";
        ThrottlingKafkaConsumer consumerRate2 = new ThrottlingKafkaConsumer(
                broker,
                topic,
                "groupId",
                "2",
                "1000");
        consumerRate2.start();
        ThrottlingKafkaConsumer consumerRate5 = new ThrottlingKafkaConsumer(
                broker,
                topic,
                "groupId",
                "5",
                "2000");
        consumerRate5.start();
        Thread.sleep(5000);
        ThrottlingKafkaProducer producer = new ThrottlingKafkaProducer(broker, "producerId");
        IntStream.range(0, 100).forEach(i -> producer.publishMessage("key", consumerRate2.partition, ("hello world " + i).getBytes(), topic));
        IntStream.range(0, 100).forEach(i -> producer.publishMessage("key", consumerRate5.partition, ("hello world " + i).getBytes(), topic));
        Thread.sleep(10000);
    }

    /**
     * Kafka consumer wrapper that receive in constructor arguemts like topic, groupId, and
     * rateLimit and interval, to create a Throttling consumer, that it will consume with a
     * max number of records in a specific interval time.
     */
    static public class ThrottlingKafkaConsumer {

        public final String broker;
        public final String topic;
        public final String groupId;
        public final String rateLimit;
        public final String interval;
        public Integer partition;
        public Consumer<String, byte[]> consumer;
        public ScheduledFuture<?> scheduledFuture;

        /**
         * For this pattern we configure the option [max.poll.records] which specify how many records can download in each [poll]
         * doing this we can handle the max rate per time iteration.
         */
        public ThrottlingKafkaConsumer(
                String broker,
                String topic,
                String groupId,
                String rateLimit,
                String interval) {
            this.broker = broker;
            this.topic = topic;
            this.groupId = groupId;
            this.rateLimit = rateLimit;
            this.interval = interval;
        }

        public void start() {
            this.consumer = createConsumer();
            this.scheduledFuture = consumeRecords(consumer, Long.parseLong(interval));
        }

        /**
         * We configure the Kafka connect and we subscribe to the Topic, adding also a Assignment listener class, to receive the partition
         * when the broker assign to us.
         */
        private Consumer<String, byte[]> createConsumer() {
            Properties props = new Properties();
            props.put("bootstrap.servers", broker);
            props.put("group.id", groupId);
            props.put("max.poll.records", rateLimit);
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
            final KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
            consumer.subscribe(java.util.List.of(topic), partitionAssignmentListener());
            return consumer;
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

        public final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        /**
         * Since we need to have an interval of time when we want to consume and pause the consumer we use
         * a Scheduler running with a fixed rate of time. Which means we will consume the [max.poll.records] per [FixedRate] time.
         * <p>
         * It's important to understand we need to use pause/resume in Kafka consumer, because otherwise if we
         * just sleep the consumer, and we exceed the [max.poll.interval.ms] the consumer will be evicted from the consumerGroup.
         * https://medium.com/@vaibhav.vb24/kafka-confluent-pause-and-resume-consumer-cda7305944bf
         */
        public ScheduledFuture<?> consumeRecords(final Consumer<String, byte[]> consumer, long interval) {
            final Runnable consumerRate = () -> {
                consumer.resume(consumer.assignment());
                ConsumerRecords<String, byte[]> consumerRecords = consumer.poll(ofSeconds(1));
                consumerRecords.forEach(record -> System.out.printf("############ Consumer partition %s message %s ############\n", partition, new String(record.value())));
                consumer.commitAsync();
                consumer.pause(consumer.assignment());
            };
            return scheduler.scheduleAtFixedRate(consumerRate, 0, interval, MILLISECONDS);
        }
    }

    /**
     * Kafka producer implementation.
     * We invoke [publishMessage] passing as argument the partition
     * where we want to send the message.
     */
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
