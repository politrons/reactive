package akka;


import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpEntities;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.Materializer;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import io.vavr.control.Try;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class AkkaHttpFeatures {

    @Test
    public void httpServer() throws ExecutionException, InterruptedException, TimeoutException {
        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "routes");

        var materializer = Materializer.apply(system);

        httpServer(system);

        final var responseFuture = httpClient(system, "http://localhost:5555/akkaServer");

        var future = responseFuture.toCompletableFuture()
                .thenApply(response -> Try.of(() -> response.entity()
                                .toStrict(1000, materializer)
                                .toCompletableFuture()
                                .get()
                                .getData()
                                .decodeString("UTF-8"))
                        .getOrElse("Client error in communication"));

        System.out.println(future.get(10, TimeUnit.SECONDS));
    }

    @Test
    public void httpServerStreaming() throws ExecutionException, InterruptedException, TimeoutException {
        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "routes");

        var materializer = Materializer.apply(system);

        httpServerStreaming(system);

        final var responseFuture = httpClient(system, "http://localhost:5556/akkaServerStreaming");

        var future = responseFuture.toCompletableFuture()
                .thenApply(response -> Try.of(() -> response.entity()
                                .toStrict(1000, materializer)
                                .toCompletableFuture()
                                .get()
                                .getData()
                                .decodeString("UTF-8"))
                        .getOrElse("Client error in communication"));

        System.out.println(future.get(10, TimeUnit.SECONDS));
    }

    private CompletionStage<HttpResponse> httpClient(ActorSystem<Void> system, String path) {
        return Http.get(system)
                .singleRequest(HttpRequest.create(path));
    }

    private void httpServer(ActorSystem<Void> system) {
        final Http http = Http.get(system);
        var server = new AkkaHttpServer();
        http.newServerAt("localhost", 5555)
                .bind(server.createRoute())
                .whenComplete((bin, t) -> {
                    if (t != null) {
                        System.out.println("Error running server. Caused by " + t.getMessage());
                    } else {
                        System.out.println("Server online at http://localhost:5555/");
                    }
                });
    }

    private void httpServerStreaming(ActorSystem<Void> system) {
        final Http http = Http.get(system);
        var server = new AkkaHttpStreaming();
        http.newServerAt("localhost", 5556)
                .bind(server.createRoute())
                .whenComplete((bin, t) -> {
                    if (t != null) {
                        System.out.println("Error running server. Caused by " + t.getMessage());
                    } else {
                        System.out.println("Server online at http://localhost:5556/");
                    }
                });
    }

    static class AkkaHttpServer extends AllDirectives {

        private Route createRoute() {
            return concat(
                    path("akkaServer", () ->
                            get(() ->
                                    complete("Hello world from Akka http server"))));
        }

    }

    static class AkkaHttpStreaming extends AllDirectives {
        private Route createRoute() {
            final Random rnd = new Random();
            // streams are re-usable so we can define it here
            // and use it for every request
            Source<Integer, NotUsed> numbers = Source.fromIterator(() -> Stream.generate(rnd::nextInt).iterator());

            return concat(
                    path("akkaServerStreaming", () ->
                            get(() ->
                                    complete(HttpEntities.create(ContentTypes.TEXT_PLAIN_UTF8,
                                            // transform each number to a chunk of bytes
                                            numbers.map(x -> ByteString.fromString(x + "\n")))))));
        }
    }

}
