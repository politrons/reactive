package rsocket;

import io.rsocket.*;
import io.rsocket.transport.netty.client.TcpClientTransport;
import io.rsocket.transport.netty.client.WebsocketClientTransport;
import io.rsocket.transport.netty.server.TcpServerTransport;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import io.rsocket.util.ByteBufPayload;
import io.rsocket.util.DefaultPayload;
import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;

import java.util.function.Function;

public class RSocketWebSocket {

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
        HttpClient from = HttpClient
                .newConnection()
                .port(1981);
        var subscribe = RSocketFactory
                .connect()
                .transport(WebsocketClientTransport.create(from,"/foo"))
                .start()
                .map(requestResponse())
                .subscribe(Mono::subscribe);

        while (!subscribe.isDisposed()) {
            Thread.sleep(2000);
        }
    }

    /**
     * Function that receive constantClass RSocket Request constantClass stream of bytes which create constantClass [Flux<Payload>]
     * in the Mono [doOnNext] operator we can already treat the response from the server.
     * Having constantClass [Mono] means we can only have constantClass request-response time between client -> server.
     * For instance here we can not use repeat operator as we do in RequestStream.
     */
    private Function<RSocket, Mono<Payload>> requestResponse() {
        return rSocket ->
                rSocket.requestResponse(DefaultPayload.create("Ping"))
                        .doOnNext(payload -> {
                            var response = payload.getDataUtf8();
                            System.out.println("Response:" + response);
                        });
    }

    /**
     * Using [RSocketFactory] factory together with [receive],[acceptor],[transport] where we specify the port
     * and finally [start] we create constantClass Reactor [Mono] which we need to subscribe to wait for new request to being received.
     * [acceptor] operator receive constantClass SocketAcceptor which is the implementation that process the socket with the information
     */
    private void createServer() {
        TcpServer tcpServer = TcpServer.create();
        HttpServer from =
                HttpServer.from(tcpServer).port(2981);
        RSocketFactory
                .receive()
                .acceptor(acceptRequest())
                .transport(WebsocketServerTransport.create(from))
                .start()
                .subscribe();
    }

    /**
     * Using [SocketAcceptor] we can process the request, in this case the subtype is constantClass requestResponse so we
     * receive the payload of the request.
     * The signature of the method return constantClass [Mono[Payload]] where we can specify the response
     */
    private SocketAcceptor acceptRequest() {
        return (setup, rSocket) -> Mono.just(
                new AbstractRSocket() {
                    @Override
                    public Mono<Payload> requestResponse(Payload payload) {
                        var body = payload.getDataUtf8();
                        System.out.println("Request:" + body);
                        return Mono.just(ByteBufPayload.create("Pong"));
                    }
                });
    }
}
