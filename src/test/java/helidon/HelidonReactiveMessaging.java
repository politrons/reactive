package helidon;

import io.helidon.common.reactive.Multi;
import io.helidon.messaging.Channel;
import io.helidon.messaging.Messaging;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.Test;

public class HelidonReactiveMessaging {

    /**
     * Using [listener] operator, we subscribe to the channel we pass as first element, and we implement
     * a consumer function where we receive the payload of the message unwrapped. The Ack of the message is done
     * by the operator once we run the consumer function without side effects.
     */
    @Test
    public void channelFeatures() throws InterruptedException {
        var channel = Channel.<String>create("channel");

        Messaging.builder()
                .publisher(channel, Multi.just("hello", "channel", "world").map(Message::of))
                .build();

        Messaging.builder()
                .listener(channel, s -> System.out.println("Message received: " + s))
                .build()
                .start();

        Thread.sleep(2000);

    }

}
