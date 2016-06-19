package rx.utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.junit.Test;
import rx.observables.MathObservable;

import java.util.*;

import static java.util.stream.Collectors.*;


/**
 * Here we show different mechanism to revert a map from key/value to value as key and key as value.
 */
public class RevertMapObservable {


    class Product {
        Integer quantity;

        public Product(Integer quantity) {
            this.quantity = quantity;
        }
    }

    @Test
    public void test() throws InterruptedException {
        List<Product> products1 = Arrays.asList(new Product(1), new Product(2), new Product(3));
        List<Product> products2 = Arrays.asList(new Product(1), new Product(3));

        Map<String, List<Product>> productsRes = new HashMap<>();
        productsRes.put("res1", products1);
        productsRes.put("res2", products2);

        HashMap<String, List<Product>> total = rx.Observable.from(productsRes.entrySet())
                                                            .flatMap(entry -> rx.Observable.from(entry.getValue())
                                                                                           .map(product -> product.quantity)
                                                                                           .reduce((x, y) -> x + y)
                                                                                           .filter(integer -> integer > 2)
                                                                                           .compose(transformToEntry(entry)))
                                                            .reduce(new HashMap<String, List<Product>>(), (map, entry) -> {
                                                                map.put(entry.getKey(), entry.getValue());
                                                                return map;
                                                            })
                                                            .toBlocking()
                                                            .last();

        System.out.println(total);
    }

    @Test
    public void sumIntegerMathObservableFromObject() throws InterruptedException {
        List<Product> products1 = Arrays.asList(new Product(1), new Product(2), new Product(3));
        List<Product> products2 = Arrays.asList(new Product(1), new Product(1));

        Map<String, List<Product>> productsRes = new HashMap<>();
        productsRes.put("res1", products1);
        productsRes.put("res2", products2);

        HashMap<String, List<Product>> total = rx.Observable.from(productsRes.entrySet())
                                                            .flatMap(entry -> rx.Observable.from(entry.getValue())
                                                                                           .map(product -> product.quantity)
                                                                                           .toList()
                                                                                           .flatMap(list -> MathObservable.sumInteger(rx.Observable.from(list)))
                                                                                           .filter(integer -> integer > 2)
                                                                                           .compose(transformToEntry(entry)))
                                                            .reduce(new HashMap<String, List<Product>>(), (map, entry) -> {
                                                                map.put(entry.getKey(), entry.getValue());
                                                                return map;
                                                            })
                                                            .toBlocking()
                                                            .last();

        System.out.println(total);
    }


    @Test
    public void sumIntegerMathObservable() throws InterruptedException {
        List<Integer> products = Arrays.asList(1, 2, 3);
        MathObservable.sumInteger(rx.Observable.from(products))
                      .subscribe(System.out::println);


    }


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

    private rx.Observable.Transformer<Integer, Map.Entry<String, List<Product>>> transformToEntry(
            Map.Entry<String, List<Product>> entry) {return i -> rx.Observable.just(entry);}



}
