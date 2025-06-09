package com.politrons.kafka;

import io.vavr.control.Try;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.TaskMetadata;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.stream.IntStream;

import static java.time.Duration.ofSeconds;
import static org.apache.kafka.common.serialization.Serdes.*;
import static org.apache.kafka.streams.StoreQueryParameters.*;
import static org.apache.kafka.streams.StreamsConfig.*;
import static org.apache.kafka.streams.state.QueryableStoreTypes.*;


@EmbeddedKafka(partitions = 5)
public class KafkaStreamFeature {

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, true, 5, "Consumer-topic", "Consumer-topic-1", "Consumer-topic-2");

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
        //Functional operators
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

        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.start();
        System.out.println("Initializing stream");
        Thread.sleep(2000);
        publishMessages(broker, topic);
        Thread.sleep(5000);

        streams.close();
    }

    /**
     * Another feature of Kafka Stream, is to "persist" the events as a Table, and search records using [stores].
     * Once we have a Stream, we can create [KTable] with a [Materialized] with a given name to be used later on
     * to make the queries.
     * <p>
     * In order to do queries over the table we have to use [store] operator over the stream, passing
     * a [StoreQueryParameters.fromNameAndType] where we specify the KTable view name, and also the types
     * normally [QueryableStoreTypes.keyValueStore()].
     * That create the [ReadOnlyKeyValueStore] which is a view that allow us [get, all, range] over the records
     * of the Stream.
     */
    @Test
    public void queries() throws InterruptedException {
        String broker = embeddedKafkaBroker.getBrokersAsString();
        String topic = "Consumer-topic";
        //Source
        Properties config = getSourceConfig(broker);
        //Topology/Flow
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> stream = builder.stream(topic);
        //Table
        stream.flatMapValues(v -> List.of(v.toUpperCase()))
                .toTable(Materialized.as("eventsStore"));

        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.start();
        System.out.println("Initializing stream");

        Thread.sleep(2000);
        publishMessages(broker, topic);

        ReadOnlyKeyValueStore<String, String> eventsStore =
                streams.store(fromNameAndType("eventsStore", keyValueStore()));

        System.out.println("Get all:");
        KeyValueIterator<String, String> events = eventsStore.all();
        while (events.hasNext()) {
            KeyValue<String, String> next = events.next();
            System.out.println("Key " + next.key + " Value: " + next.value);
        }

        System.out.println("Range:");
        KeyValueIterator<String, String> rangeEvents = eventsStore.range("key-1", "key-5");
        while (rangeEvents.hasNext()) {
            KeyValue<String, String> next = rangeEvents.next();
            System.out.println("Key " + next.key + " Value: " + next.value);
        }

        System.out.println("Get:" + eventsStore.get("key-5"));

        streams.close();
    }

    @Test
    public void globalKTable() throws InterruptedException, IOException {
        String broker = embeddedKafkaBroker.getBrokersAsString();
        String topic = "Consumer-topic";

        publishMessages(broker, topic);

        Thread.sleep(2000);


        Properties config = getSourceConfig(broker);
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "my-global-ktable-app");
        config.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/kafka-streams");
        config.put(COMMIT_INTERVAL_MS_CONFIG, 1000);

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> stream = builder.stream(topic);
        stream.mapValues(s -> s.toUpperCase())
                .to("global-events-topic");

        GlobalKTable<String, String> globalTable = builder.globalTable(
                "global-events-topic-",
                Materialized.as("globalEventsStore")
        );

        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.start();
        System.out.println("Initializing stream");

        Thread.sleep(2000);

        ReadOnlyKeyValueStore<String, String> globalEventsStore =
                streams.store(
                        fromNameAndType("globalEventsStore", QueryableStoreTypes.keyValueStore())
                );

        System.out.println("Get all:");
        KeyValueIterator<String, String> events = globalEventsStore.all();
        while (events.hasNext()) {
            KeyValue<String, String> next = events.next();
            System.out.println(STR."Record Key \{next.key} Value: \{next.value}");
        }

        System.out.println(STR."Get: \{globalEventsStore.get("key-5")}");

        while(!readCheckpointFromGlobalKTable(streams,"my-global-ktable-app")){
            Thread.sleep(1000);
        }
        streams.close();

    }

    private static boolean readCheckpointFromGlobalKTable(KafkaStreams streams, String storeName) throws IOException {
        Set<org.apache.kafka.streams.processor.ThreadMetadata> threads = streams.localThreadsMetadata();
        for (org.apache.kafka.streams.processor.ThreadMetadata threadMetadata : threads) {
            for (TaskMetadata taskMetadata : threadMetadata.activeTasks()) {
                File storeDir = new File(new File("/tmp/kafka-streams"), storeName);
                if (storeDir.exists() && storeDir.isDirectory()) {
                    File checkpointFile = new File(storeDir , "/global/.checkpoint");
                    if (checkpointFile.exists()) {
                        String content = Files.readString(checkpointFile.toPath()).trim();
                        System.out.printf("Checkpoint in %s in task %s: %s%n",
                                storeName, taskMetadata.taskId(), content);
                        return true;
                    } else {
                        System.out.printf("File does not exist in %s%n", storeDir.getAbsolutePath());
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Test
    public void topicToTopic() throws InterruptedException {
        String broker = embeddedKafkaBroker.getBrokersAsString();
        String topic1 = "Consumer-topic-1";
        String topic2 = "Consumer-topic-2";

        Properties config = getSourceConfig(broker);
        Serde<String> stringSerde = Serdes.String();

        StreamsBuilder builder = new StreamsBuilder();
        builder.stream(topic1, Consumed.with(stringSerde, stringSerde))
                .peek((k, v) -> System.out.println("Topic 1 Observed event:" + v))
                .mapValues((ValueMapper<String, String>) String::toUpperCase)
                .to(topic2, Produced.with(stringSerde, stringSerde));
        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.start();
        Thread.sleep(2000);

        publishMessages(broker, topic1);

        StreamsBuilder builder1 = new StreamsBuilder();
        builder1.stream(topic2, Consumed.with(stringSerde, stringSerde))
                .foreach((key, value) -> System.out.println("key: " + key + " -> " + value));

        Topology topology1 = builder1.build();
        KafkaStreams streams1 = new KafkaStreams(topology1, config);
        streams1.start();
        Thread.sleep(2000);

        streams.close();
        streams1.close();
    }

    private void publishMessages(String broker, String topic) {
        KafkaStreamProducer producer = new KafkaStreamProducer(broker, "producerId");
        IntStream.range(0, 10).forEach(i -> producer.publishMessage("key-" + i, "hello world " + UUID.randomUUID(), topic));
        System.out.println("Sending events to stream");
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
