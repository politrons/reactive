package java16;

import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static java16.Java16Features.Sex.MALE;


public class Java16Features {

    /**
     * Now the [instanceof] of Java 16 allow us not only compare types in runtime, but also cast
     * to extract the value to be used in the same [if] condition
     */
    @Test
    public void newPatternMatching() {
        var output = getValueFromPatternMatching("hello pattern matching");
        System.out.println(output);
        output = getValueFromPatternMatching(1981);
        System.out.println(output);
    }

    private String getValueFromPatternMatching(Object obj) {
        if (obj instanceof String s && s.length() > 5) {
            return s.toUpperCase();
        } else if (obj instanceof Integer i && i > 10) {
            return (i + 1000) + "";
        } else {
            return "No type detected";
        }
    }

    /**
     * Finally with version 16 Record class are standard in the library and we can use
     * to implement immutable data.
     */
    @Test
    public void recordClass() {
        var name = new Name("Politrons");
        var user = new User(name, MALE);
        System.out.println(user);
    }

    record Name(String value) {
    }

    enum Sex {
        MALE, FEMALE
    }

    record User(Name name, Sex sex) {
    }

    /**
     * Since this version we're able to transform the stream into list using operator [toList] instead have tp use
     * verbose [collect] operator
     */
    @Test
    public void streamToList() {
        List<String> words =
                Stream.of("hello ", "stream ", "to list")
                        .map(String::toLowerCase)
                        .filter(s -> s.contains(" "))
                        .toList();
        System.out.println(words);
    }


    static {
        System.out.println("Hello Java 16 world.....");
    }
}
