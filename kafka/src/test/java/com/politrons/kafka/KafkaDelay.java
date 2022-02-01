package com.politrons.kafka;


import io.vavr.collection.List;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
public class KafkaDelay {

    static String delayTopic10000 = "delayTopic10000";
    static String delayTopic20000 = "delayTopic20000";

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, true, 4, delayTopic10000, delayTopic20000);

    private final EmbeddedKafkaBroker embeddedKafkaBroker = embeddedKafkaRule.getEmbeddedKafka();

    /**
     * In this example we create two instance of Kafka consumer.
     * One will have 10 seconds delay in each record we add in the topic,
     * and the other one it will have 20 seconds delay.
     */
    @Test
    public void specificPartition() throws InterruptedException {
        String broker = embeddedKafkaBroker.getBrokersAsString();
        DelayKafkaConsumer delay10000 = new DelayKafkaConsumer(
                broker,
                delayTopic10000,
                "groupId",
                "10000");
        delay10000.start();
        DelayKafkaConsumer delay20000 = new DelayKafkaConsumer(
                broker,
                delayTopic20000,
                "groupId",
                "20000");
        delay20000.start();
        Thread.sleep(5000);
        DelayKafkaProducer producerDelay1 = new DelayKafkaProducer(broker, "producerId");
        IntStream.range(0, 5).forEach(i -> {
            producerDelay1.publishMessage("key", ("hello world with delay 10s " + i).getBytes(), delayTopic10000);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        });

        Thread.sleep(30000);

        DelayKafkaProducer producerDelay2 = new DelayKafkaProducer(broker, "producerId");
        IntStream.range(0, 5).forEach(i -> {
            producerDelay2.publishMessage("key", ("hello world with delay 20s " + i).getBytes(), delayTopic20000);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        });

        Thread.sleep(60000);
    }

    /**
     * Kafka consumer wrapper that receive in constructor like topic, groupId, and delay, to create a DelayKafkaConsumer,
     * that it will consume only those records that spend the delay time configured in the Kafka Consumer.
     */
    static public class DelayKafkaConsumer {

        public final String broker;
        public final String topic;
        public final String groupId;
        public final String delay;
        public Consumer<String, byte[]> consumer;
        public ScheduledFuture<?> scheduledFuture;

        /**
         * For this pattern we configure the option [delay] which specify how much time the record need to be inside
         * the Kafka topic before we extract from there.
         */
        public DelayKafkaConsumer(
                String broker,
                String topic,
                String groupId,
                String delay) {
            this.broker = broker;
            this.topic = topic;
            this.groupId = groupId;
            this.delay = delay;
        }

        public void start() {
            this.consumer = createConsumer();
            this.scheduledFuture = consumeRecords(consumer, Long.parseLong(delay));
        }

        private Consumer<String, byte[]> createConsumer() {
            Properties props = new Properties();
            props.put("bootstrap.servers", broker);
            props.put("group.id", groupId);
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
            final KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
            consumer.subscribe(java.util.List.of(topic));
            return consumer;
        }

        public final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        private int time = 0;

        /**
         * We create a [ScheduledFuture] where we calculate the interval as the 10% of the delay time.
         * Then in each iteration we make the equation [(record.timestamp + delay) <= currentTimestamp]
         * when the equation is true, we extract the record from the Topic, otherwise we stop consumption,
         * and we set the commit offset to the current record using [seek] operator.
         */
        public ScheduledFuture<?> consumeRecords(final Consumer<String, byte[]> consumer, long delay) {
            long interval = (delay * 10) / 100;
            final Runnable consumerRate = () -> {
                consumer.resume(consumer.assignment());
                ConsumerRecords<String, byte[]> consumerRecords = consumer.poll(ofSeconds(1));
                for (ConsumerRecord<String, byte[]> record : consumerRecords) {
                    System.out.println("Interval " + time);
                    if ((record.timestamp() + delay) <= System.currentTimeMillis()) {
                        System.out.println("Record spend in Topic " + (System.currentTimeMillis() - record.timestamp()) / 1000);
                        System.out.println("Record value " + new String(record.value()));
                    } else {
                        time += interval;
                        List.ofAll(consumerRecords.partitions())
                                .find(tp -> tp.partition() == record.partition())
                                .forEach(tp -> consumer.seek(tp, record.offset()));
                        break;
                    }
                }
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
    public static class DelayKafkaProducer {

        public final String broker;
        public final String producerId;
        private final Producer<String, byte[]> producer;

        public DelayKafkaProducer(String broker, String producerId) throws IllegalArgumentException {
            this.broker = broker;
            this.producerId = producerId;
            this.producer = new KafkaProducer<>(this.getProperties());
        }

        public void publishMessage(
                String key,
                byte[] payload,
                String topic
        ) {
            Try.run(() -> {
                ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, payload);
                this.producer.send(record).get();
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
