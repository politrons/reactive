import com.google.gson.Gson;
import io.helidon.common.reactive.Multi;
import io.helidon.messaging.Channel;
import io.helidon.messaging.Messaging;
import io.helidon.messaging.connectors.kafka.KafkaConfigBuilder;
import io.helidon.messaging.connectors.kafka.KafkaConnector;
import org.apache.commons.io.FileUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.retry.RetryNTimes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class SftpConnectorDemo {

    private final String kafkaServer = "localhost:29092";
    private final String topic = "hsbc_sftp_topic";

    @Test
    public void sftpConnector() throws Exception {

        kafkaConsumer();
        Thread.sleep(600000);
    }

    int counter = 0;

    /**
     * Consumer that it should only receive events from one publisher
     */
    private void kafkaConsumer() {
        Channel<String> consumerChannel = Channel.<String>builder()
                .name("kafka.png-connector")
                .publisherConfig(KafkaConnector.configBuilder()
                        .bootstrapServers(kafkaServer)
                        .groupId("hsbc_sftp-group")
                        .topic(topic)
                        .autoOffsetReset(KafkaConfigBuilder.AutoOffsetReset.EARLIEST)
                        .enableAutoCommit(false)
                        .keyDeserializer(StringDeserializer.class)
                        .valueDeserializer(StringDeserializer.class)
                        .build()
                )
                .build();

        Messaging.builder()
                .connector(KafkaConnector.create())
                .listener(consumerChannel, payload -> {
                    SftpDocument sftpDocument = new Gson().fromJson(payload, SftpDocument.class);
                    System.out.println("Kafka event: " + sftpDocument);
                    try {
                        byte[] decodedBytes = Base64.getDecoder().decode(sftpDocument.payload);
                        FileUtils.writeByteArrayToFile(new File("image" + counter + ".png"), decodedBytes);
                        counter += 1;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                })
                .build()
                .start();
    }


    static class Schema {

        public String type;
        public boolean optional;

        public Schema(String type, boolean optional) {
            this.type = type;
            this.optional = optional;
        }

        public String getType() {
            return type;
        }

        public boolean isOptional() {
            return optional;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setOptional(boolean optional) {
            this.optional = optional;
        }
    }

    static class SftpDocument {
        public Schema schema;
        public String payload;


        public SftpDocument(Schema schema, String payload) {
            this.schema = schema;
            this.payload = payload;
        }

        public Schema getSchema() {
            return schema;
        }

        public String getPayload() {
            return payload;
        }

        public void setSchema(Schema schema) {
            this.schema = schema;
        }

        public void setPayload(String payload) {
            this.payload = payload;
        }
    }
}
