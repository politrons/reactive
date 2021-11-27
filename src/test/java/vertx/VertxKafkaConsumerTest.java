package vertx;


import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;

import io.vertx.core.Vertx;
import vertx.kafka.VertxKafkaConsumer;
import vertx.kafka.VertxKafkaProducer;

@EmbeddedKafka(partitions = 1)
public class VertxKafkaConsumerTest {

    private final Vertx vertx = Vertx.vertx();

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, true, 10, "vertx-topic");

    private final EmbeddedKafkaBroker embeddedKafkaBroker = embeddedKafkaRule.getEmbeddedKafka();

    @Test
    public void runKafkaConnector() throws InterruptedException {
        String brokersAsString = embeddedKafkaBroker.getBrokersAsString();
        vertx.deployVerticle(new VertxKafkaConsumer(brokersAsString));
        Thread.sleep(5000);
        vertx.deployVerticle(new VertxKafkaProducer(brokersAsString));
        Thread.sleep(60000);

    }
}