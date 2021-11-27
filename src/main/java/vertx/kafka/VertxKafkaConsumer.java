package vertx.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.kafka.client.consumer.KafkaConsumer;

public class VertxKafkaConsumer extends AbstractVerticle {

    private final String brokersAsString;

    public VertxKafkaConsumer(final String brokersAsString) {
        this.brokersAsString = brokersAsString;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        System.out.println("Running kafka consumer");
        String topic = "vertx-topic";
        Map<String, String> config = getKafkaConfig();
        KafkaConsumer<String, String> consumer = KafkaConsumer.create(vertx, config);
        addKafkaHandler(consumer);
        consumer.subscribe(topic)
                .onSuccess(v -> System.out.printf("Consumer subscribed to %s successfully", topic))
                .onFailure(t -> System.out.printf("[Error] Consumer not subscribed to %s. Caused by %s", topic, ExceptionUtils.getStackTrace(t)));
        startPromise.complete();
    }

    private void addKafkaHandler(final KafkaConsumer<String, String> consumer) {
        consumer.handler(record -> {
            System.out.println("Processing key=" + record.key() + ",value=" + record.value() +
                    ",partition=" + record.partition() + ",offset=" + record.offset());
        });
    }

    private Map<String, String> getKafkaConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", brokersAsString);
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("group.id", "my_group");
        config.put("auto.offset.reset", "earliest");
        config.put("enable.auto.commit", "false");
        return config;
    }
}