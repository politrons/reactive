package helidon;

import io.helidon.common.http.Http;
import io.helidon.common.reactive.Single;
import io.helidon.webclient.WebClient;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.jersey.JerseySupport;
import io.vavr.control.Try;
import org.junit.Test;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.concurrent.TimeUnit;

public class HelidonWebServer {

    @Test
    public void webServerFeatures() {
        final int port = webServer();
        Single<String> single = createClient(port)
                .get()
                .path("/user")
                .request(String.class);

        final Try<String> response = Try.of(() -> single.get(10, TimeUnit.SECONDS));
        System.out.println(response);
    }

    @Test
    public void jerseyServerFeatures() {
        final int port = jerseyServer();
        Single<String> single = createClient(port)
                .get()
                .path("/jersey/user")
                .request(String.class);

        final Try<String> response = Try.of(() -> single.get(10, TimeUnit.SECONDS));
        System.out.println(response);
    }

    /**
     * Using [WebClient.builder()] pattern we can create a WebClient to add later specific information to make request to server.
     * Here we add all the general configuration for the client, like baseUri, what we want to do with redirects, TLS communication,
     * connection timeout,
     */
    private WebClient createClient(int port) {
        return WebClient.builder()
                .baseUri("http://localhost:" + port)
                .followRedirects(true)
                .connectTimeout(2000, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Standard Http web server. It's composed just like many iother webservers like Vertx where we define a Routing instance
     * with all the endpoints, and for each we define a BiConsumer function<Request, Response> handler.
     * <p>
     * Then using [WebServer.create] factory passing the routing created previously we create and instance of [WebServer]
     * and once we decide to start it, we just use [start] operator, which it returns a Single<WebServer> which is running async.
     * <p>
     * [error] operator offers the error handling where we can catch whatever type of Exception we define in this operator passing the
     * class type and then implementing a function  with three inputs(request, response and error)
     */
    public static int webServer() {
        final Routing routing = Routing.builder()
                .get("/user", (req, res) -> res.send("Pablo Picouto Garcia"))
                .get("/nickname", (req, res) -> res.send("Politrons"))
                .get("/age", (req, res) -> res.send("40"))
                .error(Exception.class, (req, res, ex) -> {
                    res.status(Http.Status.INTERNAL_SERVER_ERROR_500);
                    res.send("Unhandled error in server. Caused by " + ex.getMessage());
                })
                .build();

        final WebServer webServer = WebServer.create(routing)
                .start()
                .await(10, TimeUnit.SECONDS);

        System.out.println("Server started at: http://localhost:" + webServer.port());
        return webServer.port();
    }


    /**
     * Jersey Http server is a framework on top of standard [JAX-RS] which provide annotations and async servlet.
     * In order to be able to plug this feature in Helidon web server, we have to use [register] where we add the
     * path pattern where all endpoints will inherit. Then we create a Service using [JerseySupport.builder()] pattern
     * where we have to register the class where we implement Jersey endpoints.
     */
    public static int jerseyServer() {
        final Routing routing = Routing.builder()
                .register("/jersey",
                        JerseySupport.builder()
                                .register(JerseyServer.class)
                                .build())
                .build();

        final WebServer webServer = WebServer.create(routing)
                .start()
                .await(10, TimeUnit.SECONDS);

        System.out.println("Jersey Server started at: http://localhost:" + webServer.port());
        return webServer.port();
    }

    @Path("/")
    @RequestScoped
    public static class JerseyServer {

        @Context
        private ServerRequest request;

        @Context
        private ServerResponse response;

        @GET
        @Path("user")
        public Single<ServerResponse> user() {
            return response.send("Hello Politrons from Jersey World Helidon!");
        }
    }
}
