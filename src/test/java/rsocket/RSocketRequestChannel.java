package rsocket;

import io.rsocket.*;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.ByteBufPayload;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Function;

import static io.rsocket.util.DefaultPayload.create;

public class RSocketRequestChannel {

    @Test
    public void main() throws InterruptedException {
        createServer();
        createClient();
    }

    /**
     * Using [RSocketFactory] open a connecton using [transport] operator and specify [TcpClientTransport] in a port.
     * Then we use [start] operator that create a [Mono<RSocket>] then we subscribe to the Mono
     */
    private void createClient() throws InterruptedException {
        var subscribe = RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(1981))
                .start()
                .map(requestChannel())
                .subscribe(Flux::subscribe);

        while (!subscribe.isDisposed()) {
            Thread.sleep(5000);
        }
    }

    /**
     * Function that receive a RSocket Request a publisher which create a [Flux<Payload>]
     * Having a publisher between client-server is like have a pipeline between client-server.
     * In this example we send every element through the pipeline withe speed and throughput specify by client or server.
     * In the Flux [doOnNext] operator we can already treat the response from the server for every iteration.
     * Having a [Flux] means can have back-pressure between client -> server.
     * Here for instance we can limit the number of elements send using [take] operator
     * In this example we make the back-pressure in the server part with [delay] operator
     */
    private Function<RSocket, Flux<Payload>> requestChannel() {
        var payloads = Flux.just(create("hello"), create("back-pressure"), create("between"), create("client"), create("server"), create("foo"), create("bla"));
        return rSocket ->
                rSocket.requestChannel(payloads)
                        .take(5)
                        .doOnNext(payload -> {
                            var response = payload.getDataUtf8();
                            System.out.println("Response:" + response);
                        });
    }

    /**
     * Using [RSocketFactory] factory together with [receive],[acceptor],[transport] where we specify the port
     * and finally [start] we create a Reactor [Mono] which we need to subscribe to wait for new request to being received.
     * [acceptor] operator receive a SocketAcceptor which is the implementation that process the socket with the information
     */
    private void createServer() {
        RSocketFactory
                .receive()
                .acceptor(acceptRequest())
                .transport(TcpServerTransport.create(1981))
                .start()
                .subscribe();
    }

    /**
     * Using [SocketAcceptor] we can process the request, in this case the subtype is a requestChannel so we
     * receive the publisher of the request.
     * The signature of the method return a [Flux[Payload]] where we specify the response for every element send
     * in the pipeline between client-server
     */
    private SocketAcceptor acceptRequest() {
        return (setup, rSocket) -> Mono.just(
                new AbstractRSocket() {
                    @Override
                    public Flux<Payload> requestChannel(Publisher<Payload> publisher) {
                        return Flux.from(publisher)
                                .map(Payload::getDataUtf8)
                                .delayElements(Duration.ofMillis(500))
                                .map(String::toUpperCase)
                                .map(body -> {
                                    System.out.println("Request:" + body);
                                    return ByteBufPayload.create("-" + body + "-");
                                });
                    }
                });
    }
}
