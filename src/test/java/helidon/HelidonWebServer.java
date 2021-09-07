package helidon;

import io.helidon.common.reactive.Single;
import io.helidon.webclient.WebClient;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import io.vavr.control.Try;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class HelidonWebServer {

    @Test
    public void webServerFeatures() {
        final int port = server();

        var client = WebClient.builder()
                .baseUri("http://localhost:" + port)
                .followRedirects(true)
                .build();

        Single<String> single = client.get()
                .path("/user")
                .request(String.class);

        final Try<String> response = Try.of(() -> single.get(10, TimeUnit.SECONDS));
        System.out.println(response);

    }

    public static int server() {
        final Routing routing = Routing.builder()
                .get("/user", (req, res) -> res.send("Pablo Picouto Garcia"))
                .get("/nickname", (req, res) -> res.send("Politrons"))
                .get("/age", (req, res) -> res.send("40"))
                .build();

        final WebServer webServer = WebServer.create(routing)
                .start()
                .await(10, TimeUnit.SECONDS);

        System.out.println("Server started at: http://localhost:" + webServer.port());
        return webServer.port();
    }
}
