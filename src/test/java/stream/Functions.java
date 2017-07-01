package stream;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;


/**
 * Functional programing is the cabality that allow us to send or return functions which contains an implementation.
 * Those implementations are related with three diferent interfaces which we will cover here.
 * *
 *
 * @author Pablo Perez
 */
public class Functions {


    /**
     * In this example we use a Function, which receive an item and then return the same or another item through the pipeline.
     * Is the function used by mutable operators as Map or FlatMap
     *
     * @throws InterruptedException
     */
    @Test
    public void functionFunction() throws InterruptedException {
        String words = Stream.of("hello_functional_world")
                .map(replaceWordsFunction())
                .map(String::toUpperCase)
                .reduce("", String::concat);

        System.out.println(words);
    }

    private Function<String, String> replaceWordsFunction() {
        return string -> string.replace("_", " ");
    }

    /**
     * In this example we use a Consumer function, a function which receive an argument and does not return anything since is void.
     * that´s why the name consumer because only consume the items passed and do not propagate any item in the pipeline.
     * Can be consider as the end of the pipeline.
     *
     * @throws InterruptedException
     */
    @Test
    public void consumerFunction() throws InterruptedException {
        Arrays.asList("hello", "functional", "world")
                .stream()
                .forEach(upperWordsFunction());

    }

    private Consumer<String> upperWordsFunction() {
        return word -> {
            word = word.toUpperCase();
            System.out.println(word);
        };
    }

    /**
     * Predicate function is just a boolean function which receive an item and return true/false
     *
     * @throws InterruptedException
     */
    @Test
    public void predicateFunction() throws InterruptedException {
        String words = Stream.of("hello ", "OOD", "functional ", "world")
                .filter(isAFunctionalWorldFunction())
                .reduce("", String::concat);

        System.out.println(words);
    }

    private Predicate<String> isAFunctionalWorldFunction() {
        return word -> word.trim().equals("hello") || word.trim().equals("functional") || word.trim().equals("world");
    }

    /**
     * Supplier function does not receive any argument, and just return a value
     *
     * @throws InterruptedException
     */
    @Test
    public void supplierFunction() throws InterruptedException {
        Stream.of("Actual time:")
                .map(s -> s.concat(String.valueOf(systemCurrentFunction().get())))
                .forEach(System.out::println);

    }

    private Supplier<Long> systemCurrentFunction() {
        return System::currentTimeMillis;
    }

    /**
     * In this example we can see how we can combine the three types of functions in the same pipeline,
     * to provide to our pipeline all the logic that it needs.
     *
     * @throws InterruptedException
     */
    @Test
    public void allFunctionsCombined() throws InterruptedException {
        Stream.of("hello_Foo_functional_OOD_world_!_")
                .map(replaceWordsFunction())
                .map(splitWordsFunction())
                .flatMap(ws -> ws.stream()
                        .filter(isAFunctionalWorldFunction()))
                .forEach(upperWordsFunction());

    }

    private Function<String, List<String>> splitWordsFunction() {
        return a -> Arrays.asList(a.split(" "));
    }

    @Test
    public void supplier() {

    }


}
