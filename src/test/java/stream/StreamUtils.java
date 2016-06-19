package stream;

import io.vertx.core.json.JsonObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


/**
 * In this class we cover with particle examples all Stream API, showing how easy to implement is,
 * and how internally works using the most common Java 8 functions( Consumer, Predicate, Function)
 *
 * @author Pablo Perez
 */
public class StreamUtils {


    /**
     * Map is simple operator to evolve the value of items emitted in your pipeline
     * In this example we start with a string fruit apple, and we finish with a banana
     * Shall print
     * I´ a banana
     *
     * @throws InterruptedException
     */
    @Test
    public void mapStream() throws InterruptedException {
        String fruit = Stream.of("apple")
                             .map(a -> "pinapple")
                             .map(p -> "orange")
                             .map(o -> "bannana")
                             .reduce("", String::concat);

        System.out.println("I´ a " + fruit);
    }

    /**
     * Using the operator flatMap allow you to return another Stream which will be processed before the next step in your pipeline.
     * once the stream become eager.
     * In this case we see, how the first flatMap step will return a new stream with a map to pineapple1
     * So the next filter will apply and continue the pipeline
     * Shall print
     * I´ a Banana
     *
     * @throws InterruptedException
     */
    @Test
    public void flatMapStream() throws InterruptedException {
        String fruit = Stream.of("apple")
                             .flatMap(a -> Stream.of("pineapple")
                                                 .map(p -> "pineapple1"))
                             .filter(p -> p.equals("pineapple1"))
                             .flatMap(p -> Stream.of("orange"))
                             .flatMap(o -> Stream.of("banana")
                                                 .map(b -> "Banana"))
                             .reduce("", String::concat);

        System.out.println("I´ a " + fruit);
    }

    /**
     * Filter operator just receive a predicate function, and continue the pipeline if that function return true
     *
     * @throws InterruptedException
     */
    @Test
    public void filterStream() throws InterruptedException {
        String test = "works";
        JsonObject product = new JsonObject().put("A", "1");
        Stream.of(product)
              .filter(p -> test.equals("works"))
              .forEach(p -> product.put("B", 2));

        System.out.println(product);
    }

    /**
     * Collect is one of the operators that transform our pipeline form lazy to eager, make it start emitting the items.
     * Pick up the items emitted and collect into a Collector
     *
     * @throws InterruptedException
     */
    @Test
    public void collectStream() throws InterruptedException {
        List<Integer> list = Arrays.asList(2, 1, 13, 11, 5)
                                   .stream()
                                   .sorted()
                                   .collect(toList());
        System.out.println(list);
    }

    /**
     * Like collect this operator execute the terminal and make the pipeline eager from lazy.
     * this operator start with an initial value, and then as second argument we pass a BiFunction
     * where we pass the previous emitted item and the new one.
     *
     * @throws InterruptedException
     */
    @Test
    public void reduceStream() throws InterruptedException {
        Integer total = Arrays.asList(1, 2, 3)
                              .stream()
                              .reduce(0, (integer, integer2) -> integer + integer2);
        System.out.println(total);
    }

    /**
     * This operator just run the Predicate function and return true/false as the result of the function.
     *
     * @throws InterruptedException
     */
    @Test
    public void matchStream() throws InterruptedException {
        boolean match = Arrays.asList(1, 2, 3)
                              .stream()
                              .anyMatch(integer -> integer > 2);
        System.out.println(match);
    }

    /**
     * This operator is not a terminal executor. It just filter the items emitted and only pass those that has not been emitted already
     *
     * @throws InterruptedException
     */
    @Test
    public void distinctStream() throws InterruptedException {
        List<Integer> list = Arrays.asList(1, 2, 3, 1, 4, 2, 3)
                                   .stream()
                                   .distinct()
                                   .collect(toList());
        System.out.println(list);
    }


    /**
     * Operator that limit the total number of items emitted through the pipeline
     *
     * @throws InterruptedException
     */
    @Test
    public void limitStream() throws InterruptedException {
        List<Integer> list = Arrays.asList(1, 2, 3, 1, 4, 2, 3)
                                   .stream()
                                   .limit(3)
                                   .collect(toList());
        System.out.println(list);
    }


    /**
     * Peek operator just run a Consumer function, which we pass the item emitted but we cannot send a different item through the pipeline.
     * It would be similar to foreach, but this one always emit the Stream(T), not like foreach which is void.
     *
     * @throws InterruptedException
     */
    @Test
    public void peekStream() throws InterruptedException {
        List<Integer> list = Arrays.asList(1, 2, 3)
                                   .stream()
                                   .peek(number -> System.out.println("This consume function is void, we not modify the stream in here"))
                                   .collect(toList());
        System.out.println(list);
    }

    /**
     * Similar to limit, but the other way around, this operator skip the first specific items emitted through the pipeline
     *
     * @throws InterruptedException
     */
    @Test
    public void skipStream() throws InterruptedException {
        List<Integer> list = Arrays.asList(1, 2, 3)
                                   .stream()
                                   .skip(1)
                                   .collect(toList());
        System.out.println(list);
    }

    /**
     * Return the copy of the stream once the close method is invoked
     *
     * @throws InterruptedException
     */
    @Test
    public void onCloseStream() throws InterruptedException {
        List<Integer> list = Arrays.asList(1, 2, 3)
                                   .stream()
                                   .onClose(() -> {
                                   })
                                   .peek(System.out::println)
                                   .collect(toList());
        System.out.println(list);
    }

    //Comparators

    /**
     * This operator just get the max value emitted by the pipeline
     * @throws InterruptedException
     */
    @Test
    public void maxStream() throws InterruptedException {
        Integer list = Arrays.asList(5, 2, 1, 3)
                             .stream()
                             .max(Comparator.comparingInt(i -> i))
                             .get();
        System.out.println(list);
    }

    /**
     * In case of use String it will return the higher value of the chart
     * Shall print D
     * @throws InterruptedException
     */
    @Test
    public void maxStreamString() throws InterruptedException {
        String list = Arrays.asList("A", "C", "D", "B")
                             .stream()
                             .max(Comparator.comparing(i -> i))
                             .get();
        System.out.println(list);
    }

    /**
     * This operator just get the min value emitted by the pipeline
     * @throws InterruptedException
     */
    @Test
    public void minStream() throws InterruptedException {
        Integer list = Arrays.asList(5, 2, 1, 3)
                             .stream()
                             .min(Comparator.comparingInt(i -> i))
                             .get();
        System.out.println(list);
    }

    /**
     * In case of use String it will return the higher value of the chart
     * Shall print A
     * @throws InterruptedException
     */
    @Test
    public void minStreamString() throws InterruptedException {
        String list = Arrays.asList("A", "C", "D", "B")
                            .stream()
                            .max(Comparator.comparing(i -> i))
                            .get();
        System.out.println(list);
    }

}
