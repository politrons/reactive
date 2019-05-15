package vavr;

import io.vavr.collection.List;
import org.junit.Test;

import java.util.ArrayList;

public class VavrCollections {

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
}
