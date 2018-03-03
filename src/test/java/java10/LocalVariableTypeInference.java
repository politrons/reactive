package java10;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * Java 10 finally introduce local variable type inference which allow for local variable to dont have to
 * specify the type since is inference already by the signature of the method, class that you consume.
 * Not like in Scala here we can only use in local scope, and not in global. And also still not distinctions between
 * mutable(var) and immutable(val) variables.
 * <p>
 * In order to test and c those new capabilities you will need IDE that works with it
 * https://www.jetbrains.com/idea/nextversion/
 */
public class LocalVariableTypeInference {


    @Test
    public void main() {
        var str = "hello to inference type world";
        System.out.println(str);

        var listOfTypes = List.of("is", "cool", "not", "to", "type", "so", "much");
        System.out.println(listOfTypes);

        var sentence = getHelloWithoutType();
        System.out.println(sentence);

        var stream = Stream.of("Hello", "Java", "9", "10")
                .filter(value -> !value.equals("9"))
                .map(String::toUpperCase);
        stream.forEach(System.out::println);

        var numbers = IntStream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .filter(number -> number <= 5)
                .map(number -> number * 10)
                .boxed()
                .collect(Collectors.toUnmodifiableList());
        System.out.println(numbers);

        var testClass = new TestClass();
        var _sentence = testClass.sentence;
        System.out.println(_sentence);

    }

    private String getHelloWithoutType() {
        return "Hello without type";
    }

    private class TestClass {
        String sentence = "You looks like more like Scala";
    }


}
