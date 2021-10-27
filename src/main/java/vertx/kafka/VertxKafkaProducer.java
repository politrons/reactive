package vertx.kafka;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;

import java.util.HashMap;
import java.util.Map;

public class VertxKafkaProducer extends AbstractVerticle {

    private final String brokersAsString;

    public VertxKafkaProducer(final String brokersAsString) {
        this.brokersAsString = brokersAsString;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        System.out.println("Running kafka producer");
        String topic = "vertx-topic";
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", brokersAsString);
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("acks", "1");

        KafkaProducer<String, String> producer = KafkaProducer.create(vertx, config);
        KafkaProducerRecord<String, String> record = KafkaProducerRecord.create(topic, "hello world");
        producer.send(record)
                .onSuccess(recordMetadata ->
                        System.out.println(
                                "Message " + record.value() + " written on topic=" + recordMetadata.getTopic() +
                                        ", partition=" + recordMetadata.getPartition() +
                                        ", offset=" + recordMetadata.getOffset()
                        )
                );
        startPromise.complete();
    }
}