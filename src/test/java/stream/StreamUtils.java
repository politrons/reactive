package stream;

import io.vertx.core.json.JsonObject;
import org.junit.Test;
import rx.observables.transforming.Person;

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
    public void mapStream() {
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
    public void flatMapStream() {
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
     * Shall return
     * {"A":"1","B":2}
     *
     * @throws InterruptedException
     */
    @Test
    public void filterStream() {
        String test = "works";
        JsonObject product = new JsonObject().put("A", "1");
        Stream.of(product)
                .filter(p -> test.equals("works"))
                .forEach(p -> product.put("B", 2));

        System.out.println(product);
    }

    /**
     * Collect is one of the operators that transform our pipeline form lazy to eager, make it start emitting the items.
     * In this pipeline we use the operator sorted to sort the items emitted
     * Pick up the items emitted and collect into a Collector
     * Shall print
     * [1, 2, 5, 11, 13]
     *
     * @throws InterruptedException
     */
    @Test
    public void collectStream() {
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
     * Shall print
     * 6
     *
     * @throws InterruptedException
     */
    @Test
    public void reduceStream() {
        Integer total = Arrays.asList(1, 2, 3)
                .stream()
                .reduce(0, (integer, integer2) -> integer + integer2);
        System.out.println(total);
    }

    /**
     * This operator just run the Predicate function and return true/false as the result of the function.
     * shall print
     * true
     *
     * @throws InterruptedException
     */
    @Test
    public void matchStream() {
        boolean match = Arrays.asList(1, 2, 3)
                .stream()
                .anyMatch(integer -> integer > 2);
        System.out.println(match);
    }

    /**
     * This operator is not a terminal executor. It just filter the items emitted and only pass those that has not been emitted already
     * Shall print
     * [1, 2, 3, 4]
     *
     * @throws InterruptedException
     */
    @Test
    public void distinctStream() {
        List<Integer> list = Arrays.asList(1, 2, 3, 1, 4, 2, 3)
                .stream()
                .distinct()
                .collect(toList());
        System.out.println(list);
    }


    /**
     * Operator that limit the total number of items emitted through the pipeline
     * Shall print
     * [1, 2, 3]
     *
     * @throws InterruptedException
     */
    @Test
    public void limitStream() {
        List<Integer> list = Arrays.asList(1, 2, 3, 1, 4, 2, 3)
                .stream()
                .limit(3)
                .collect(toList());
        System.out.println(list);
    }


    /**
     * Peek operator just run a Consumer function, which we pass the item emitted but we cannot send a different item through the pipeline.
     * It would be similar to foreach, but this one always emit the Stream(T), not like foreach which is void.
     * Shall print
     * This Consumer function is void, we not modify the stream in here
     * This Consumer function is void, we not modify the stream in here
     * This Consumer function is void, we not modify the stream in here
     * [1, 2, 3]
     *
     * @throws InterruptedException
     */
    @Test
    public void peekStream() {
        List<Integer> list = Arrays.asList(1, 2, 3)
                .stream()
                .peek(number -> System.out.println("This Consumer function is void, we not modify the stream in here"))
                .collect(toList());
        System.out.println(list);
    }

    /**
     * Similar to limit, but the other way around, this operator skip the first specific items emitted through the pipeline
     * Shall print
     * [2, 3]
     *
     * @throws InterruptedException
     */
    @Test
    public void skipStream() {
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
    public void onCloseStream() {
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
     * Shall print
     * 5
     *
     * @throws InterruptedException
     */
    @Test
    public void maxStream() {
        Integer list = Arrays.asList(5, 2, 1, 3)
                .stream()
                .max(Comparator.comparingInt(i -> i))
                .get();
        System.out.println(list);
    }

    /**
     * In case of use String it will return the higher value of the chart
     * Shall print D
     *
     * @throws InterruptedException
     */
    @Test
    public void maxStreamString() {
        String list = Arrays.asList("A", "C", "D", "B")
                .stream()
                .max(Comparator.comparing(i -> i))
                .get();
        System.out.println(list);
    }

    /**
     * This operator just get the min value emitted by the pipeline
     * Shall print
     * 1
     *
     * @throws InterruptedException
     */
    @Test
    public void minStream() {
        Integer list = Arrays.asList(5, 2, 1, 3)
                .stream()
                .min(Comparator.comparingInt(i -> i))
                .get();
        System.out.println(list);
    }

    /**
     * In case of use String it will return the higher value of the chart
     * Shall print A
     *
     * @throws InterruptedException
     */
    @Test
    public void minStreamString() {
        String list = Arrays.asList("A", "C", "D", "B")
                .stream()
                .max(Comparator.comparing(i -> i))
                .get();
        System.out.println(list);
    }

    /**
     * A simple example that prove that instance inside the stream are mutable from outside the stream
     * Only collections are immutable
     */
    @Test
    public void mutateObject() {
        Person person = new Person("name", 35, "male");
        Thread thread = new Thread(() -> {
            Person modifiedPerson = getPerson(person);
            System.out.println("Modified person:" + modifiedPerson.toString());
        });
        thread.start();
        person.setName("Pablo");
        System.out.println("I´ a " + person.toString());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void immutableObject() {
        Person person = new Person("name", 35, "male");
        Person modifierPerson= person.copy();
        Thread thread = new Thread(() -> {
            Person modifiedPerson = getPerson(modifierPerson);
            System.out.println("Modified person:" + modifiedPerson.toString());
        });
        thread.start();
        person.setName("Pablo");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("I´ a " + person.toString());
    }

    private Person getPerson(Person person) {
        return Stream.of(person)
                .map(person1 -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return person1;
                })
                .findAny().get();
    }

}
