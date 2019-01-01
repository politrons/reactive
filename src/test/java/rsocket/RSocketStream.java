package rsocket;

import io.rsocket.*;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.ByteBufPayload;
import io.rsocket.util.DefaultPayload;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class RSocketStream {

    @Test
    public void streamCommunication() throws InterruptedException {
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
                .map(requestStream())
                .subscribe(Flux::subscribe);

        while (!subscribe.isDisposed()) {
            Thread.sleep(2000);
        }
    }

    /**
     * Function that receive a RSocket Request a stream of bytes which create a [Flux<Payload>]
     * in the Flux [doOnNext] operator we can already treat the response from the server.
     * Having this control we can have back-pressure between client -> server.
     * For instance here we can repeat this process 5 times, sending data.
     */
    private Function<RSocket, Flux<Payload>> requestStream() {
        return rSocket ->
                rSocket.requestStream(DefaultPayload.create("Ping"))
                        .doOnNext(payload -> {
                            var response = payload.getDataUtf8();
                            System.out.println("Response:" + response);
                        }).repeat(5);
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
     * Using [SocketAcceptor] we can process the request, in this case the subtype is a requestStream so we
     * receive the payload of the request.
     * The signature of the method return a [Flux[Payload]] where we specify the response
     */
    private SocketAcceptor acceptRequest() {
        return (setup, rSocket) -> Mono.just(
                new AbstractRSocket() {
                    @Override
                    public Flux<Payload> requestStream(Payload payload) {
                        var body = payload.getDataUtf8();
                        System.out.println("Request:" + body);
                        return Flux.generate(
                                sink -> {
                                    var response = ByteBufPayload.create("Pong");
                                    sink.next(DefaultPayload.create(response));
                                    sink.complete();

                                });
                    }
                });
    }
}
