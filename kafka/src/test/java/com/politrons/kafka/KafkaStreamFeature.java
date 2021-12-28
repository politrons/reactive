package com.politrons.kafka;

import io.vavr.control.Try;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;

import static java.time.Duration.ofSeconds;
import static org.apache.kafka.common.serialization.Serdes.*;
import static org.apache.kafka.streams.StreamsConfig.*;


@EmbeddedKafka(partitions = 2)
public class KafkaStreamFeature {

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, true, 2, "Consumer-topic");

    private final EmbeddedKafkaBroker embeddedKafkaBroker = embeddedKafkaRule.getEmbeddedKafka();

    /**
     * Kafka Stream is not reactive just like Akka stream or Observable, so it does not introduce patterns like
     * [Backpressure] or [Async] computation, but introduce the concept of streaming
     * data from Kafka topic to another topic or file system.
     * <p>
     * It compose of three parts:
     * [Source] which it would be just the configuration of the Kafka consumer to connect to Kafka broker and topic,
     * and how we´´e gonna deserialize the events we receive.
     * [Topology/Flow] Where Using [StreamsBuilder] we can use builder pattern tom create this [Topology] instance.
     * Then using this builder we can create a [KStream] using the [stream] operator, and then use all functional
     * operators. Filter, Transformation, Composition.
     * [Sink] It would be the output of the stream. Where we decide where do we want to pass the events we receive in the stream.
     * In another Topic or FileSystem.
     * <p>
     * In this example we just print into console the output of each event after we use some operators of the strem
     */
    @Test
    public void stream() throws InterruptedException {
        String broker = embeddedKafkaBroker.getBrokersAsString();
        String topic = "Consumer-topic";
        //Source
        Properties config = getSourceConfig(broker);
        //Topology/Flow
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream = builder.stream(topic);

        KTable<String, String> kTable = stream
                .map((key, value) -> new KeyValue<>(key.toUpperCase(), value))// Transformation key, value
                .mapValues(value -> value.toUpperCase() + UUID.randomUUID()) // Transformation value
                .flatMap((key, value) -> List.of(new KeyValue<>(key, value))) // Composition key, value
                .flatMapValues(value -> List.of("[" + value + "]")) // Composition value
                .filter((key, value) -> value.contains("a")) // Filter
                .toTable();
        //Sink
        kTable.toStream()
                .foreach((key, value) -> System.out.println("key: " + key + " -> " + value));

        //Run
        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.start();
        System.out.println("Initializing stream");
        Thread.sleep(2000);
        KafkaStreamProducer producer = new KafkaStreamProducer(broker, "producerId");
        IntStream.range(0, 10).forEach(i -> producer.publishMessage("key-" + i, "hello world ", topic));
        System.out.println("Sending events to stream");
        Thread.sleep(5000);

        streams.close();
    }

    @Test
    public void queries() throws InterruptedException {
        String broker = embeddedKafkaBroker.getBrokersAsString();
        String topic = "Consumer-topic";
        //Source
        Properties config = getSourceConfig(broker);
        //Topology/Flow
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream = builder.stream(topic);
        stream.toTable(Materialized.as("myEvents"));

        //Run
        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.start();
        System.out.println("Initializing stream");
        Thread.sleep(2000);
        KafkaStreamProducer producer = new KafkaStreamProducer(broker, "producerId");
        IntStream.range(0, 10).forEach(i -> producer.publishMessage("key-" + i, "hello world " + UUID.randomUUID(), topic));

        System.out.println("Sending events to stream");

        ReadOnlyKeyValueStore<String, String> myEvents =
                streams.store(StoreQueryParameters.fromNameAndType(
                        "myEvents", QueryableStoreTypes.keyValueStore()));

        KeyValueIterator<String, String> events = myEvents.all();
        while (events.hasNext()) {
            KeyValue<String, String> next = events.next();
            System.out.println("Key " + next.key + " Value: " + next.value);
        }

        System.out.println("Value searched:" + myEvents.get("key-5"));

        streams.close();
    }

    private Properties getSourceConfig(String broker) {
        Properties config = new Properties();
        config.put(APPLICATION_ID_CONFIG, "My-kafka-stream");
        config.put(BOOTSTRAP_SERVERS_CONFIG, broker);
        config.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, String().getClass().getName());
        config.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, String().getClass().getName());
        return config;
    }


    /**
     * Kafka producer implementation.
     * We invoke [publishMessage] passing as argument the partition
     * where we want to send the message.
     */
    public static class KafkaStreamProducer {

        public final String broker;
        public final String producerId;
        private final Producer<String, String> producer;

        public KafkaStreamProducer(String broker, String producerId) throws IllegalArgumentException {
            this.broker = broker;
            this.producerId = producerId;
            this.producer = new KafkaProducer<>(this.getProperties());
        }

        public Try<Void> publishMessage(
                String key,
                String payload,
                String topic
        ) {
            return Try.run(() -> {
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, payload);
                this.producer.send(record).get();
            }).onFailure(t -> System.out.println("Error sending events. Caused by " + ExceptionUtils.getStackTrace(t)));
        }

        private Properties getProperties() {
            Properties props = new Properties();
            props.put("bootstrap.servers", this.broker);
            props.put("client.id", this.producerId);
            props.put("timeout.ms", "10000");
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            return props;
        }

    }

}
