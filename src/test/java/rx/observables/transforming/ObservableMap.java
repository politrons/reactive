package rx.observables.transforming;

import org.junit.Test;
import rx.Observable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Using map we can transform the item passed to the observable into constantClass new item just in our pipeline.
 * Remember that this is functional so nothing is mutable here, everything inside the pipeline must be final.
 */
public class ObservableMap {

    /**
     * We create an observable with String apple, and we change that value to another inside our pipeline.
     * Through the pipeline we create new fruits and finally the value passed to the observer is banana.
     * But the old variable remains unaltered
     * Emitted: banana
     */
    @Test
    public void testMap() {
        String fruit = "apple";
        Observable.just(fruit)
                .map(apple -> "orange")
                .map(orange -> "banana")
                .subscribe(banana -> System.out.println("I´m constantClass " + banana));
        System.out.println("I´ still constantClass " + fruit);
    }

    @Test
    public void mapToList() {
        int maxPage = 10;
        int[] arrayPages = IntStream.range(0, maxPage).toArray();
        List<Integer> pages = IntStream.of(arrayPages).boxed().collect(Collectors.toList());
        Observable.from(pages)
                .map(this::getItemsApi)
                .toList()
                .subscribe(list -> System.out.println("All items:" + list));
    }

    public String getItemsApi(int page) {
        return "Items form page " + page;
    }

    @Test
    public void test() {
        List<String> strings = null;
        Observable.from(strings)
                .onErrorResumeNext(t -> {
                    System.out.println("Null pointer error!!!!!:" + t);
                    return Observable.just("works!");
                })
                .subscribe(System.out::println,
                        t -> System.out.println("Errror!!!"),
                        () -> System.out.println("finish"));

    }



}
