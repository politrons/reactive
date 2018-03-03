package java10;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Collections {


    /**
     * Java 10 finally introduce factories to create maps with initial values avoiding the boilerplate of creating the
     * Collection.
     * Also introduce the possibility to instead of mutate a Map you can create a new map from previous one without
     * all streaming copy code.
     */
    @Test
    public void mapFeatures() {
        var map = Map.of("hello", "Java 10");
        var copyMap = Map.copyOf(map);
        System.out.println(copyMap);

        var multiMapValue = Map.of("That´s", "cool", "multiple", "key", "values", "map");
        System.out.println(multiMapValue);

        var entry = Map.entry("new", "entry");
        var mapFromEntry = Map.ofEntries(entry);
        System.out.println(mapFromEntry);
    }

    /**
     * New feature in Map factory is that it will throw an Exception if one element it´s duplicated as key.
     * Pretty disappointed with Oracle, I would expect another behaviour, like return a multimap instead.
     */
    @Test(expected = IllegalArgumentException.class)
    public void mapFeaturesDuplicityCheck() {
        var map = Map.of("hello", "Java 10", "hello", "it will fail");
        System.out.println(map);

    }

    /**
     * Also like Map collection List introduce all factories with same capabilities
     */
    @Test
    public void listFeatures() {
        var list = List.of("hello", "java", "10", "world").stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        System.out.println(list);

        var copyList = List.copyOf(List.of("hello", "Java 10"));
        System.out.println(copyList);

    }

    /**
     * New feature in Set factory is that it will throw an Exception is one element it´s duplicated
     */
    @Test(expected = IllegalArgumentException.class)
    public void setFeatures() {
        var set = Set.of("hello", "hello", "java", "10", "world").stream()
                .map(String::toUpperCase)
                .collect(Collectors.toSet());
        System.out.println(set);
    }

    /**
     * The new implementation of Collectors class allow you to return immutable List, Set and Maps
     * after, if you try to add something in that element you will receive a UnsupportedOperationException
     * In my personal opinion this is really dangerous and developers should give a clear name to avoid
     * UnsupportedOperationException become in the new NullPointerException
     */
    @Test(expected = UnsupportedOperationException.class)
    public void immutableCollections() {
        var immutableList = List.of("hello", "java", "10", "world").stream()
                .map(String::toUpperCase)
                .collect(Collectors.toUnmodifiableList());
        boolean should_not_works = immutableList.add("should not works");
        System.out.println(should_not_works);

    }


}
