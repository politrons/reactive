package com.politrons.kafka.sagas;

import io.vavr.Function0;
import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import io.vavr.control.Try;
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
import java.util.function.Consumer;

import static io.vavr.API.*;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;
import static java.time.Duration.ofSeconds;

@EmbeddedKafka(partitions = 4)
public class KSaga {

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, true, 4, "ServiceA", "ServiceB", "ServiceACompensation");

    private final EmbeddedKafkaBroker embeddedKafkaBroker = embeddedKafkaRule.getEmbeddedKafka();

    String brokers = embeddedKafkaBroker.getBrokersAsString();

    @Test
    public void sagasPattern() throws InterruptedException {

        KSaga.withAction(this::actionA)
                .withCompensation(error -> System.out.println("Reverting local transaction service A. Caused by " + new String(error)))
                .withNextServiceChannel(Some("ServiceB"))
                .withCompensationChannel(Some("ServiceACompensation"))
                .withPrevCompensationChannel(None())
                .withConfig(brokers, "ServiceA");

        KSaga.withAction(this::actionB)
                .withCompensation(error -> System.out.println("reverting local transaction service B. Caused by " + error))
                .withNextServiceChannel(None())
                .withCompensationChannel(None())
                .withPrevCompensationChannel(Some("ServiceACompensation"))
                .withConfig(brokers, "ServiceB");

        Thread.sleep(5000);

        //Send event to ServiceA to start the transaction
        KSagaProducer<byte[]> sagaProducer = new KSagaProducer<>(brokers, "initTransaction");
        sagaProducer.producer.send(new ProducerRecord<>("ServiceA", "Init transaction".getBytes()));

        Thread.sleep(60000);

    }

    private byte[] actionA() {
        var msg = "Running local transaction service A";
        System.out.println(msg);
        return msg.getBytes();
    }

    private byte[] actionB() {
        String msg = "Running local transaction service B";
        System.out.println(msg);
        System.out.println("Local error in Service B");
        throw new IllegalStateException();
    }

    //  DSL
    //-------
    /*
    Here we define the algebras that compound the DSL that help us to build the
    Saga Executor Coordinator(SEC)
    We define actions/compensations functions that it will be executed when receive an event of
    Action or Compensation.
     */
    public static <T> Action<T> withAction(Function0<T> action) {
        return new Action<>(action);
    }

    record Action<T>(Function0<T> function) {

        public Compensation<T> withCompensation(Consumer<T> compensation) {
            return new Compensation<>(this, compensation);
        }
    }

    record Compensation<T>(Action<T> action, Consumer<T> function) {

        public NextService<T> withNextServiceChannel(Option<String> actionTopic) {
            return new NextService<>(this, actionTopic);
        }
    }

    record NextService<T>(Compensation<T> compensation, Option<String> maybeActionTopic) {

        public PrevCompensationChannel<T> withCompensationChannel(Option<String> compensationTopic) {
            return new PrevCompensationChannel<>(this, compensationTopic);
        }
    }

    record PrevCompensationChannel<T>(NextService<T> actionChannel, Option<String> maybeCompensationTopic) {

        public CompensationChannel<T> withPrevCompensationChannel(Option<String> maybePrevCompensationTopic) {
            return new CompensationChannel<>(this, maybePrevCompensationTopic);
        }
    }

    record CompensationChannel<T>(PrevCompensationChannel<T> prevCompensationChannel,
                                  Option<String> maybePrevCompensationTopic) {

        public void withConfig(String broker, String serviceTopic) {
            saga(broker, serviceTopic);

        }

        // Interpreter of the KSaga DSL
        //------------------------------
        private void saga(String broker, String serviceTopic) {

            /*
             * Consumer to subscribe to possible compensation action over local transaction
             */
            Option<KSagaConsumer<T>> maybeKafkaConsumerCompensation =
                    prevCompensationChannel.maybeCompensationTopic.map(compensationTopic -> new KSagaConsumer<>(
                            broker,
                            compensationTopic,
                            "groupId"));
            /*
             * Consumer to subscribe to possible compensation action over local transaction
             */
            KSagaConsumer<T> serviceConsumer =
                    new KSagaConsumer<>(
                            broker,
                            serviceTopic,
                            "groupId");

            /*
              Producer to send the action output to the next service of the platform
             */
            KSagaProducer<T> kSagaProducer =
                    new KSagaProducer<>(
                            broker,
                            "actionProducer"
                    );

            /*
              Producer to send back to the previous service the reason why the distributed transaction
              fail, so then this service can do the compensation
             */
            KSagaProducer<byte[]> kSagaProducerError =
                    new KSagaProducer<>(
                            broker,
                            "actionProducer"
                    );

            /*
               In this input function we control Side-effect of the action, in case of success, we send the output of the action
               to the next service in the platform.
               And in case of error, we send event error back to the previous service to allow him to perform a compensation.
             */
            Consumer<T> inputFunction =
                    input -> {
                        System.out.println("Saga started " + serviceTopic);
                        Try.of(prevCompensationChannel.actionChannel.compensation.action.function::apply)
                                .onSuccess(output -> Match(prevCompensationChannel.actionChannel.maybeActionTopic).of(
                                        Case($Some($()), actionTopic -> {
                                            ProducerRecord<String, T> record =
                                                    new ProducerRecord<>(actionTopic, output);
                                            return kSagaProducer.producer.send(record);
                                        }),
                                        Case($None(), "empty")
                                ))
                                .onFailure(t -> Match(maybePrevCompensationTopic).of(
                                        Case($Some($()), prevCompensationTopic -> {
                                            System.out.println("Error found sending message for compensation back to " + prevCompensationTopic);
                                            ProducerRecord<String, byte[]> record =
                                                    new ProducerRecord<>(prevCompensationTopic, "Critical error".getBytes());
                                            return kSagaProducerError.producer.send(record);
                                        }),
                                        Case($None(), "empty")
                                ));
                    };


            Future.run(() -> Match(maybeKafkaConsumerCompensation).of(
                    Case($Some($()), compensationConsumer -> {
                        compensationConsumer.start(prevCompensationChannel.actionChannel.compensation.function);
                        return "";
                    }),
                    Case($None(), "empty")
            ));
            Future.run(() -> {
                serviceConsumer.start(inputFunction);
            });
        }
    }

    // Kafka Transport Layer
    //------------------------

    //Simple Kafka consumer implementation
    static public class KSagaConsumer<T> {

        public final String broker;
        public final String topic;
        public final String groupId;
        public org.apache.kafka.clients.consumer.Consumer<String, T> consumer;

        public KSagaConsumer(
                String broker,
                String topic,
                String groupId) {
            this.broker = broker;
            this.topic = topic;
            this.groupId = groupId;
        }

        /*
            We start the consumer subscription receiving a Generic Consumer<T> function, to apply
            in case we receive an error response event to make a compensation.
         */
        public void start(Consumer<T> compensationFunc) {
            this.consumer = createConsumer();
            consumeRecords(consumer, compensationFunc);
        }

        private org.apache.kafka.clients.consumer.Consumer<String, T> createConsumer() {
            Properties props = new Properties();
            props.put("bootstrap.servers", broker);
            props.put("group.id", groupId);
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
            final KafkaConsumer<String, T> consumer = new KafkaConsumer<>(props);
            consumer.subscribe(List.of(topic).toJavaList());
            return consumer;
        }

        public void consumeRecords(final org.apache.kafka.clients.consumer.Consumer<String, T> consumer, Consumer<T> function) {
            while (true) {
                ConsumerRecords<String, T> consumerRecords = consumer.poll(ofSeconds(1));
                if (!consumerRecords.isEmpty()) {
                    consumerRecords.forEach(record -> {
                        System.out.println("Kafka event received in topic:" + record.topic());
                        function.accept(record.value());
                    });
                    consumer.commitAsync();
                }
            }
        }
    }

    //Simple Kafka producer implementation
    public static class KSagaProducer<T> {

        public final String broker;
        public final String producerId;
        private final Producer<String, T> producer;

        public KSagaProducer(String broker, String producerId) throws IllegalArgumentException {
            this.broker = broker;
            this.producerId = producerId;
            this.producer = new KafkaProducer<>(this.getProperties());
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
