package com.politrons.kafka;


import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Node;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static com.politrons.kafka.KafkaBalancing.ThrottlingKafkaConsumer.*;
import static java.time.Duration.ofSeconds;

/**
 * Using Apache Kafka [AdminClient] we're able to do un runtime potential things like create Topics on the fly.
 */
@EmbeddedKafka(partitions = 4)
public class KafkaAdminClient {

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, true, 4, "non_used");

    private final EmbeddedKafkaBroker embeddedKafkaBroker = embeddedKafkaRule.getEmbeddedKafka();

    private static final String NEW_TOPIC = "New-Topic";

    /**
     * A Kafka Consumer/Producer that is sending events to a new Topic created by Kafka Consumer.
     */
    @Test
    public void adminClient() throws InterruptedException, ExecutionException, TimeoutException {
        String broker = embeddedKafkaBroker.getBrokersAsString();
        KafkaConsumerAdminClient consumer = new KafkaConsumerAdminClient(
                broker,
                List.of("High", "Medium"),
                "groupId");
        consumer.start();
        Thread.sleep(5000);

        KafkaProducerAdminClient producer = new KafkaProducerAdminClient(broker, "producerId");

        Future.run(() -> IntStream.range(0, 100).forEach(i -> {
            producer.publishMessage("key", ("hello world " + i).getBytes(), NEW_TOPIC);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }));
        Thread.sleep(60000);
    }

    static public class KafkaConsumerAdminClient {

        public final String broker;
        public final List<String> topic;
        public final String groupId;
        public Consumer<String, byte[]> consumer;

        public KafkaConsumerAdminClient(
                String broker,
                List<String> topic,
                String groupId) {
            this.broker = broker;
            this.topic = topic;
            this.groupId = groupId;
        }

        /**
         * Using [AdminClient] we are able to do multiple actions across the network between client and broker.
         * In this example we do the next actions:
         * * Create a new Topic [NEW_TOPIC] which we will use right away from our client.
         * * Create a new partition into the Topic [NEW_TOPIC] so then client it will join to the two partitions.
         * * Get Topic information (Leader, Partitions, Replicas)
         * * Get the information of the Cluster (id, host, port, if is in a rack)
         */
        public void start() throws ExecutionException, InterruptedException {
            AdminClient adminClient = AdminClient.create(getProperties());
            System.out.println("######## Creating new Topic ##########");
            short replica = 1;
            CreateTopicsResult topicsResult = adminClient.createTopics(List.of(new NewTopic(NEW_TOPIC, 1, replica)));
            topicsResult.all().get();
            DescribeClusterResult describeClusterResult = adminClient.describeCluster();
            System.out.println("######## Creating new Partition ##########");
            CreatePartitionsResult newPartition = adminClient.createPartitions(Map.of(NEW_TOPIC, NewPartitions.increaseTo(2)));
            newPartition.all().get();
            System.out.println("######## Topic Info ##########");
            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(List.of(NEW_TOPIC));
            Map<String, TopicDescription> topicsDescription = describeTopicsResult.all().get();
            for (TopicDescription tp : topicsDescription.values()) {
                System.out.println(tp.toString());
            }
            System.out.println("######## Cluster Info ##########");
            System.out.println(describeClusterResult.clusterId().get());
            Node node = describeClusterResult.controller().get();
            System.out.println(node.host());
            System.out.println(node.port());
            System.out.println(node.hasRack());
            this.consumer = createConsumer(List.of(NEW_TOPIC));
            Future.run(() -> consumeRecords(consumer));
        }

        private Consumer<String, byte[]> createConsumer(List<String> topic) {
            Properties props = getProperties();
            final KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
            consumer.subscribe(topic);
            return consumer;
        }

        private Properties getProperties() {
            Properties props = new Properties();
            props.put("bootstrap.servers", broker);
            props.put("group.id", groupId);
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
            return props;
        }

        public void consumeRecords(final Consumer<String, byte[]> consumer) {
            while (true) {
                ConsumerRecords<String, byte[]> consumerRecords = consumer.poll(ofSeconds(5));
                consumerRecords.forEach(record -> System.out.printf("############ Consumer topic %s message %s ############\n", record.topic(), new String(record.value())));
                consumer.commitAsync();
            }
        }
    }
}
