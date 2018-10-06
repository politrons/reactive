package java11;

import org.junit.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.*;

/**
 * With the new http client by default it will try to connect using HTTP 2.0 protocol.
 * If the server does not supports HTTP/2, then HTTP/1.1 will be used.
 * <p>
 * In order to make work this code is mandatory create your module-info.java class
 * and import the module [requires java.net.http;]
 */
public class HttpClient2Feature {


    @Test
    public void httpRequest1() throws InterruptedException, ExecutionException, TimeoutException {
        makeRequest("http://www.google.com");
    }

    @Test
    public void httpRequest2() throws InterruptedException, ExecutionException, TimeoutException {
        makeRequest("https://www.google.com");
    }

    /**
     * The API provide two builders, one to create the [httpClient] where you can specify protocol version,
     * Timeout using Duration API which has also updates new in 11, also you can specify a policy for redirects.
     *
     * The second builder is for the [HttpRequest] which allow you obviously to specify method, add the URI of the request,
     * header or headers(weird/dangerous how works)
     *
     * Then finally using the client we can do [sync] and [async] calls. In case of Async it will return a CompletableFuture
     */
    private void makeRequest(String uri) throws InterruptedException, ExecutionException, TimeoutException {
        var httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)  // this is the default
                .connectTimeout(Duration.ofMillis(1500))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        var request = HttpRequest
                .newBuilder(URI.create(uri))
                .GET()
                .setHeader("my_custom_header","hello java 11")
                .build();
        var future = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        var status = future
                .thenApply(response -> {
                    System.out.println("Communication done in : " + response.version());
                    return response;
                })
                .thenApply(HttpResponse::statusCode)
                .get(10, SECONDS);
        System.out.println("Response call:" + status);
    }

}
