package com.politrons.kafka.sagas;

import io.vavr.Function0;
import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import io.vavr.control.Try;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.ClassRule;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;

import java.util.Properties;
import java.util.function.Consumer;

import static java.time.Duration.ofSeconds;

@EmbeddedKafka(partitions = 4)
public class KSaga {

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, true, 4, "ServiceA", "ServiceB");

    private final static EmbeddedKafkaBroker embeddedKafkaBroker = embeddedKafkaRule.getEmbeddedKafka();

    static String brokers = embeddedKafkaBroker.getBrokersAsString();

    public static void main(String[] args) {
        KSaga.withAction(() -> {
                    var msg = "Running local transaction service A";
                    System.out.println(msg);
                    return msg;
                })
                .withCompensation(() -> "reverting local transaction service A")
                .withActionChannel("ServiceB")
                .withCompensationChannel("")
                .build();

        KSaga.withAction(() -> {
                    System.out.println("Running local transaction service B");
                    throw new IllegalStateException();
                })
                .withCompensation(() -> "reverting local transaction service B")
                .withActionChannel("")
                .withCompensationChannel("ServiceA")
                .build();

    }

    public static <T> Action <T> withAction(Function0<T> action) {
        return new Action<>(action);
    }

    record Action<T>(Function0<T> function) {

        public Compensation<T> withCompensation(Function0<T> compensation) {
            return new Compensation<>(this, compensation);
        }
    }

    record Compensation<T>(Action<T> action, Function0<T> compensation) {

        public ActionChannel<T> withActionChannel(String actionTopic) {
            return new ActionChannel<>(this, actionTopic);
        }
    }

    record ActionChannel<T>(Compensation<T> compensation, String actionTopic) {

        public CompensationChannel<T> withCompensationChannel(String compensationTopic) {
            return new CompensationChannel<>(this, compensationTopic);
        }
    }

    record CompensationChannel<T>(ActionChannel<T> actionChannel, String compensationTopic) {

        public void build() {

            KSagaConsumer compensationConsumer =
                    new KSagaConsumer(
                            null,
                            compensationTopic,
                            "groupId");

            KSagaProducer<T> kSagaProducer =
                    new KSagaProducer<>(
                            null,
                            "actionProducer"
                    );

            KSagaProducer<byte[]> kSagaProducerError =
                    new KSagaProducer<>(
                            null,
                            "actionProducer"
                    );

            Try.of(actionChannel.compensation.action.function::apply)
                    .onSuccess(output -> {
                        ProducerRecord<String, T> record =
                                new ProducerRecord<>(compensationTopic, output);
                        kSagaProducer.producer.send(record);
                    })
                    .onFailure(t -> {
                        ProducerRecord<String, byte[]> record =
                                new ProducerRecord<>(compensationTopic, t.getMessage().getBytes());
                        kSagaProducerError.producer.send(record);
                    });

            compensationConsumer.start();

        }
    }


    static public class KSagaConsumer {

        public final String broker;
        public final String topic;
        public final String groupId;
        public org.apache.kafka.clients.consumer.Consumer<String, byte[]> consumer;

        public KSagaConsumer(
                String broker,
                String topic,
                String groupId) {
            this.broker = broker;
            this.topic = topic;
            this.groupId = groupId;
        }

        public void start() {
            this.consumer = createConsumer();
            Future.run(() -> consumeRecords(consumer));
        }

        private org.apache.kafka.clients.consumer.Consumer<String, byte[]> createConsumer() {
            Properties props = new Properties();
            props.put("bootstrap.servers", broker);
            props.put("group.id", groupId);
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
            final KafkaConsumer<String, byte[]> consumer = new KafkaConsumer<>(props);
            consumer.subscribe(List.of(topic).toJavaList());
            return consumer;
        }

        public void consumeRecords(final org.apache.kafka.clients.consumer.Consumer<String, byte[]> consumer) {
            while (true) {
                ConsumerRecords<String, byte[]> consumerRecords = consumer.poll(ofSeconds(5));
                consumerRecords.forEach(record -> System.out.printf("############ Consumer topic %s message %s ############\n", record.topic(), new String(record.value())));
                consumer.commitAsync();
            }
        }
    }

    public static class KSagaProducer<T> {

        public final String broker;
        public final String producerId;
        private final Producer<String, T> producer;

        public KSagaProducer(String broker, String producerId) throws IllegalArgumentException {
            this.broker = broker;
            this.producerId = producerId;
            this.producer = new KafkaProducer<>(this.getProperties());
        }

        public Try<String> publishMessage(
                String key,
                T payload,
                String topic
        ) {
            return Try.of(() -> {
                ProducerRecord<String, T> record = new ProducerRecord<String, T>(topic, key, payload);
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
