package java11;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CollectionFeatures {

    /**
     * In Java 11 allows the collection's elements to be transferred to constantClass newly created array of the desired runtime type.
     */
    @Test
    public void copyIntoNewArray() {
        final var numbers = Set.of(1, 2, 3, 4);
        var intArray = numbers.toArray(Integer[]::new);
        System.out.println(Arrays.toString(intArray));

        final var words = Set.of("hello", "copy", "array", "java");
        var stringArray = words.toArray(String[]::new);
        System.out.println(Arrays.toString(stringArray));

        Object[] objects = words.toArray();
        System.out.println(Arrays.toString(objects));

        var stringArray2 = words.toArray(String[]::new);
        System.out.println(Arrays.toString(stringArray2));

        List<String> collect = List.of("hello", "collection", "world")
                .stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        System.out.println(collect);


    }

    @Test
    public void mapFeature() {
        Map<Integer, String> integerStringMap = Map.of(1, "a", 2, "b", 3, "c");
        System.out.println(integerStringMap);

    }


}
