package com.politrons.kafka;


import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import org.apache.kafka.clients.producer.*;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofSeconds;

/**
 * Here we cover the pattern of how we can create multiple Kafka consumer under the same topic.
 * Using different rate limit/interval configuration. So we can have multiple throttling consumers,
 * and once the broker assign a partition to each of them we can start sending messsge to them, to the
 * specific partition
 */
@EmbeddedKafka(partitions = 4)
public class KafkaProducerRetry {

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, true, 4);

    private final EmbeddedKafkaBroker embeddedKafkaBroker = embeddedKafkaRule.getEmbeddedKafka();

    /**
     * In this example we create two instance of Kafka consumer.
     * One will have 10 seconds delay in each record we add in the topic,
     * and the other one it will have 20 seconds delay.
     */
    @Test
    public void retrySend() throws IOException {
        String broker = embeddedKafkaBroker.getBrokersAsString();
        RetryKafkaProducer retryKafkaProducer = new RetryKafkaProducer(broker, "producerId");
        embeddedKafkaBroker.destroy();
        embeddedKafkaBroker.getZookeeper().shutdown();
        Future<RecordMetadata> future = retryKafkaProducer.publishMessage("key", "hello world with retries 10s ".getBytes(), "NonExistTopic");
        RecordMetadata recordMetadata = future.await(30, TimeUnit.SECONDS).get();
        System.out.println(recordMetadata);
    }

    public static class RetryKafkaProducer {

        public final String broker;
        public final String producerId;
        private final Producer<String, byte[]> producer;

        public RetryKafkaProducer(String broker, String producerId) throws IllegalArgumentException {
            this.broker = broker;
            this.producerId = producerId;
            this.producer = new KafkaProducer<>(this.getProperties());
        }

        public Future<RecordMetadata> publishMessage(
                String key,
                byte[] payload,
                String topic
        ) {
            ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, payload);
            return Future.of(() -> this.producer.send(record).get());
        }

        private Properties getProperties() {
            Properties props = new Properties();
            props.put("bootstrap.servers", this.broker);
            props.put("client.id", this.producerId);
            props.put("timeout.ms", "5000");
            props.put("acks", "all");
            //Set the number of retries - retries
            props.put(ProducerConfig.RETRIES_CONFIG, 3);
            //Request timeout - request.timeout.ms
            props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 15_000);
            //Only retry after one second.
            props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 5_000);
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
            return props;
        }

    }

}
