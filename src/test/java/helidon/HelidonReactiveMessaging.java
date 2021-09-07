package helidon;

import io.helidon.common.reactive.Multi;
import io.helidon.messaging.Channel;
import io.helidon.messaging.Messaging;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class HelidonReactiveMessaging {

    /**
     * [Channel] allow us to create communication layer between publisher and subscriber, between different threads.
     * Here we have a [publisher] in one thread, and a [consumer] in another thread.
     *
     * Using [publisher] we can emmit messages into the channel, we just need to specify the channel, and then pass a Publisher
     * [Multi]
     *
     * Using [listener] operator, we subscribe to the channel we pass as first element, and we implement
     * a consumer function where we receive the payload of the message unwrapped. The Ack of the message is done
     * by the operator once we run the consumer function without side effects.
     */
    @Test
    public void channelFeatures() throws InterruptedException {
        var channel = Channel.<String>create("channel");

        CompletableFuture.runAsync(() ->
                Messaging.builder()
                        .publisher(channel, Multi.just("hello", "channel", "world")
                                .map(Message::of))
                        .build()
        );

        CompletableFuture.runAsync(() ->
                Messaging.builder()
                        .listener(channel, s -> {
                            System.out.println("Computation in Thread:" + Thread.currentThread().getName());
                            System.out.println("Message received:" + s);
                        })
                        .build()
                        .start());

        Thread.sleep(2000);

    }

}
