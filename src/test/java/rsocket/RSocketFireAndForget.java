package rsocket;

import io.rsocket.*;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.util.DefaultPayload;
import org.junit.Test;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class RSocketFireAndForget {

    @Test
    public void main() throws InterruptedException {
        createServer();
        createClient();
    }

    /**
     * Using [RSocketFactory] open constantClass connecton using [transport] operator and specify [TcpClientTransport] in constantClass port.
     * Then we use [start] operator that create constantClass [Mono<RSocket>] then we subscribe to the Mono
     */
    private void createClient() throws InterruptedException {
        var subscribe = RSocketFactory
                .connect()
                .transport(TcpClientTransport.create(1981))
                .start()
                .map(requestFireAndForget())
                .subscribe(Mono::subscribe);

        while (!subscribe.isDisposed()) {
            Thread.sleep(2000);
        }
    }

    /**
     * Function that receive constantClass RSocket Request constantClass fire and forget which create constantClass [Mono<Void>]
     * This patter is mean to be used to not expect any response, so we detach the resources quickly.
     */
    private Function<RSocket, Mono<Void>> requestFireAndForget() {
        return rSocket ->
                rSocket.fireAndForget(DefaultPayload.create("Ping"));
    }

    /**
     * Using [RSocketFactory] factory together with [receive],[acceptor],[transport] where we specify the port
     * and finally [start] we create constantClass Reactor [Mono] which we need to subscribe to wait for new request to being received.
     * [acceptor] operator receive constantClass SocketAcceptor which is the implementation that process the socket with the information
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
     * Using [SocketAcceptor] we can process the request, in this case the subtype is constantClass requestFireAndForget so we
     * receive the payload of the request.
     * The signature of the method return constantClass [Mono[Void]] where there is no response
     */
    private SocketAcceptor acceptRequest() {
        return (setup, rSocket) -> Mono.just(
                new AbstractRSocket() {
                    @Override
                    public Mono<Void> fireAndForget(Payload payload) {
                        var body = payload.getDataUtf8();
                        System.out.println("Fire and forget:" + body);
                        return Mono.empty();
                    }
                });
    }
}
