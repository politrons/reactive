package rx.utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.junit.Test;

import java.util.*;

import static java.util.stream.Collectors.*;


/**
 *Here we show different mechanism to revert a map from key/value to value as key and key as value.
 */
public class RevertMapObservable {


    @Test
    public void revertMapWithObservable() throws InterruptedException {
        List<String> restrictions = Arrays.asList("res1", "res2");
        Map<String, List<String>> productsRes = new HashMap<>();
        productsRes.put("product1", restrictions);
        productsRes.put("product2", restrictions);


        rx.Observable.from(productsRes.entrySet())
                     .flatMap(entry -> rx.Observable.from(entry.getValue())
                                                    .map(restriction -> {
                                                        Multimap<String, String> multimap = ArrayListMultimap.create();
                                                        multimap.put(restriction, entry.getKey());
                                                        return multimap;
                                                    }))
                     .scan((lastItemEmitted, newItem) -> {
                         newItem.putAll(lastItemEmitted);
                         return newItem;
                     })
                     .map(Multimap::asMap)
                     .subscribe(System.out::println);
    }


    @Test
    public void revertMapWithStream() throws InterruptedException {
        List<String> restrictions = Arrays.asList("res1", "res2");
        Map<String, List<String>> productsRes = new HashMap<>();
        productsRes.put("product1", restrictions);
        productsRes.put("product2", restrictions);

        Map<Object, Collection<Object>> resProducts = productsRes.keySet()
                                                                 .stream()
                                                                 .flatMap(productId -> productsRes.get(productId)
                                                                                                  .stream()
                                                                                                  .map(restriction -> {
                                                                                                      Multimap<String, List<String>> multimap =
                                                                                                              ArrayListMultimap.create();
                                                                                                      multimap.put(restriction, Arrays.asList(productId));
                                                                                                      return multimap;
                                                                                                  }))
                                                                 .collect(ArrayListMultimap::create, (newMap, oldMap) -> newMap.putAll(oldMap),
                                                                          ArrayListMultimap::putAll)
                                                                 .asMap();
        System.out.println(resProducts);


    }

    @Test
    public void revertMapWithStreamAndAbstractMap() throws InterruptedException {
        List<String> restrictions = Arrays.asList("res1", "res2");
        Map<String, List<String>> productsRes = new HashMap<>();
        productsRes.put("product1", restrictions);
        productsRes.put("product2", restrictions);

        Map<String, List<String>> resProducts = productsRes.entrySet()
                                                           .stream()
                                                           .flatMap(entry -> entry.getValue()
                                                                                  .stream()
                                                                                  .map(restriction -> new AbstractMap.SimpleEntry<>(entry.getKey(),
                                                                                                                                    restriction)))
                                                           .collect(groupingBy(Map.Entry::getValue, mapping(Map.Entry::getKey, toList())));

        System.out.println(resProducts);


    }





}
