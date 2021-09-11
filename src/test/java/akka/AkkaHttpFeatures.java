package akka;


import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import akka.stream.Materializer;
import io.vavr.control.Try;
import org.junit.Test;

import java.util.concurrent.*;

public class AkkaHttpFeatures {

    @Test
    public void httpServer() throws ExecutionException, InterruptedException, TimeoutException {
        ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "routes");

        var materializer = Materializer.apply(system);

        httpServer(system);
        final CompletionStage<HttpResponse> responseFuture = httpClient(system);

        CompletableFuture<String> future = responseFuture.toCompletableFuture()
                .thenApply(response -> Try.of(() -> response.entity()
                                .toStrict(1000, materializer)
                                .toCompletableFuture()
                                .get()
                                .getData()
                                .decodeString("UTF-8"))
                        .getOrElse("Client error in communication"));

        System.out.println(future.get(10, TimeUnit.SECONDS));
    }

    private CompletionStage<HttpResponse> httpClient(ActorSystem<Void> system) {
        return Http.get(system)
                .singleRequest(HttpRequest.create("http://localhost:5555/akkaServer"));
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

    static class AkkaHttpServer extends AllDirectives {

        private Route createRoute() {
            return concat(
                    path("akkaServer", () ->
                            get(() ->
                                    complete("Hello world from Akka http server"))));
        }

    }

}
