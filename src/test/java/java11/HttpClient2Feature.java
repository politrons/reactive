package java11;

import org.junit.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Finally with Java 11 HttpClient is mature enough and is not in the incubator package.
 * In order to make work this code is mandatory create your module-info.java class
 * and import the module [requires java.net.http;]
 *
 * With the http client by default it will try to connect using HTTP 2.0 protocol.
 * If the server does not supports HTTP/2, then HTTP/1.1 will be used.
 */
public class HttpClient2Feature {


    /**
     * In this example google over http run in http/1.1
     */
    @Test
    public void httpRequest1() throws InterruptedException, ExecutionException, TimeoutException {
        makeRequest("http://www.google.com");
    }

    /**
     * In this example google over https run in http/2.0
     */
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
        var httpClient = createHttpClient();
        var request = createHttpRequest(uri);
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

    private HttpRequest createHttpRequest(String uri) {
        return HttpRequest
                .newBuilder(URI.create(uri))
                .GET()
                .setHeader("my_custom_header","hello java 11")
                .build();
    }

    private HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)  // this is the default
                .connectTimeout(Duration.ofMillis(1500))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }

}
