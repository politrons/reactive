package vavr;

import io.vavr.API;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import org.junit.Test;

import java.util.ArrayList;
import java.util.function.Function;

import static io.vavr.API.*;

public class VavrCollections {

    Map<String, String> actors = HashMap.of(
            "episode1", "Anakin, Owi-wan, Qui-Gon-Jin",
            "episode2", "Anakin, Owi-wan, Mace-Windu",
            "episode3", "Anakin, Owi-wan, Palpatine");

    @Test
    public void listFeatures() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        System.out.println("list:" + list);

        Number sum = List.of(1, 2, 3, 4, 5).sum();
        System.out.println("Sum value:" + sum);

        var integers = List.of(1, 2, 3, 4, 5)
                .foldLeft(new ArrayList<Integer>(),
                        (listFold, nextElement) -> {
                            listFold.add(nextElement * 100);
                            return listFold;
                        });
        System.out.println(integers);
    }

    @Test
    public void foldFeature() {
        StringBuffer stringBuffer = List.of(1, 2, 3, 4)
                .foldLeft(new StringBuffer(), (acc, next) -> acc.append("-").append(next));
        println(stringBuffer.toString());
    }

    @Test
    public void foldLeftEmpty() {
        HashMap<String, String> empty = HashMap.empty();
        Integer integer = empty.foldLeft(1981, (prev, nextV) -> prev);
        System.out.println(integer);
    }

    @Test
    public void putMap() {
        HashMap<String, String> map = HashMap.of("hello", "world");
        map = map.merge(HashMap.of("new", "world"));
        map.forEach((k, v) -> System.out.println("Key " + k + " value " + v));
    }

}
