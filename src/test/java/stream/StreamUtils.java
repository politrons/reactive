package stream;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Pablo Perez
 */
public class StreamUtils {


    @Test
    public void testStream() throws InterruptedException {
        String test = "works";
        JsonObject product = new JsonObject().put("A", "1");
        Stream.of(product)
              .filter(p -> test.equals("works"))
              .forEach(p -> product.put("B", 2));

        System.out.println(product);
    }

    @Test
    public void collectStream() throws InterruptedException {
        List<Integer> list = Arrays.asList(2, 1, 13, 11, 5)
                                   .stream()
                                   .sorted()
                                   .collect(Collectors.toList());
        System.out.println(list);
    }

    @Test
    public void reduceStream() throws InterruptedException {
        Integer total = Arrays.asList(1, 2, 3)
                              .stream()
                              .reduce(0, (integer, integer2) -> integer + integer2);
        System.out.println(total);
    }

    @Test
    public void matchStream() throws InterruptedException {
        boolean match = Arrays.asList(1, 2, 3)
                              .stream()
                              .anyMatch(integer -> integer > 2);
        System.out.println(match);
    }

    @Test
    public void distinctStream() throws InterruptedException {
        List<Integer> list = Arrays.asList(1, 2, 3, 1, 4, 2, 3)
                                   .stream()
                                   .distinct()
                                   .collect(Collectors.toList());
        System.out.println(list);
    }


    @Test
    public void limitStream() throws InterruptedException {
        List<Integer> list = Arrays.asList(1, 2, 3, 1, 4, 2, 3)
                                   .stream()
                                   .limit(3)
                                   .collect(Collectors.toList());
        System.out.println(list);
    }


    @Test
    public void peekStream() throws InterruptedException {
        List<Integer> list = Arrays.asList(1, 2, 3)
                                   .stream()
                                   .peek(number -> System.out.println("This consume function is void, we not modify the stream in here"))
                                   .collect(Collectors.toList());
        System.out.println(list);
    }

    @Test
    public void skipStream() throws InterruptedException {
        List<Integer> list = Arrays.asList(1, 2, 3)
                                   .stream()
                                   .skip(1)
                                   .collect(Collectors.toList());
        System.out.println(list);
    }

    /**
     * Return the copy of the stream once the close method is invoked
     * @throws InterruptedException
     */
    @Test
    public void onCloseStream() throws InterruptedException {
        List<Integer> list = Arrays.asList(1, 2, 3)
                                   .stream()
                                   .onClose(() -> {})
                                   .peek(System.out::println)
                                   .collect(Collectors.toList());
        System.out.println(list);
    }

    //Comparators

    @Test
    public void maxStream() throws InterruptedException {
        Integer list = Arrays.asList(5, 2,1, 3)
                             .stream()
                             .max(Comparator.comparingInt(i-> i)).get();
        System.out.println(list);
    }

    @Test
    public void minStream() throws InterruptedException {
        Integer list = Arrays.asList(5, 2,1, 3)
                             .stream()
                             .min(Comparator.comparingInt(i-> i)).get();
        System.out.println(list);
    }

}
