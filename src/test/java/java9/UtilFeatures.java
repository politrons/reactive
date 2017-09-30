package java9;

import com.sun.deploy.model.Resource;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.lang.String.*;

public class UtilFeatures {

    /**
     * New static interface method to create an immutable collection.
     */
    @Test
    public void immutableFactoryCollection() {
        List<String> letters = List.of("a", "b", "c");
        letters.parallelStream()
                .map(String::toUpperCase)
                .forEach(System.out::println);
        Map<String, String> map = Map.of("foo", "bla");
        map.forEach((key, value) -> System.out.println(format("Key %s value %s", key, value)));
    }

    @Test
    public void iteratorWithConsumer() {
        List<String> letters = List.of("a", "b", "c");
        letters.iterator().forEachRemaining(System.out::println);
    }

    /**
     * Iterate while the predicate consumer return true.
     */
    @Test
    public void intStreamWithPredicate() {
        IntStream.iterate(1, i -> i < 10, i -> i + 1)
                .forEach(System.out::println);
    }

    /**
     * Iterate for a range of numbers.
     */
    @Test
    public void intStreamWithRange() {
        IntStream.range(0, 10).forEach(System.out::println);
    }

    /**
     * With Java 9 introduce the possibility to have static and private methods in interfaces.
     */
    @Test
    public void staticInterfaceMethod() {
        MyStaticInterface.interfaceMethodWithStatic();
        SecondStaticInterface.interfaceMethodWithStatic();
    }

    public interface MyStaticInterface {
        static void interfaceMethodWithStatic() {
            init();
        }

        // This method is not part of the public API exposed by MyInterface
        private static void init() {
            System.out.println("Private method");
        }
    }

    public interface SecondStaticInterface extends MyStaticInterface {
        static void interfaceMethodWithStatic() {
            System.out.println("Static method");
        }
    }

    /**
     * Java 9 introduce some new improvements in the process API such as the able to get the processId or thr state
     * of a process.
     */
    @Test
    public void processImprovements() throws IOException {
        Process p = new ProcessBuilder("pwd").start();
        System.out.println("Current Process Id: = " + p.pid());
        System.out.println("State Process: = " + p.isAlive());
    }

    /*@Test
    public void http2Clients() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest req =
                HttpRequest.newBuilder(URI.create("http://www.google.com"))
                        .header("User-Agent", "Java")
                        .GET()
                        .build();


        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandler.asString());
        System.out.println(resp.body());
    }*/

}
