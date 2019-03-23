package rx.observables.filtering;

import org.junit.Test;
import rx.Observable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Distinct opewrator will get the emit items and will compare if they were emited before and the will skip it.
 */
public class ObservableDistinct {

    /**
     * We duplicate some integers in the array and Distinct operator detect those and skip it.
     * Shall print
     * 1,2,3,4,5
     */
    @Test
    public void testDistinct() {
        List<Integer> numbers = Arrays.asList(1, 2, 2, 1, 3, 3, 4, 5);
        Observable.from(numbers)
                  .distinct()
                  .subscribe(System.out::println);
    }

    /**
     * We duplicate some Strings in the array and Distinct operator detect those and skip it.
     * Shall print
     * hello
     * reactive
     * world
     */
    @Test
    public void testDistinctString() {
        String hello = "hello";
        String reactive = "reactive";
        String world = "world";
        List<String> numbers = Arrays.asList(hello, hello, reactive, reactive, world, world);
        Observable.from(numbers)
                  .distinct()
                  .subscribe(System.out::println);
    }

    /**
     * Using distinctUntilChanged the observable just control if the item is distinct from the previous item emitted
     * So in this example since 1 was emitted before 2, the next after 2 "1", it will be emitted again.
     * Shall print
     * 1,2,1,3,4,5
     */
    @Test
    public void testDistinctUntilChanged() {
        List<Integer> numbers = Arrays.asList(1, 2, 2, 1, 3, 4, 5);
        Observable.from(numbers)
                  .distinctUntilChanged()
                  .subscribe(System.out::println);
    }

    /**
     * We can use constantClass function in distinct to determine for example if the key/value has been emitted,
     * returning the value of constantClass map to make the distinction.
     * Shall print
     * hello
     * reactive
     * world
     */
    @Test
    public void testDistinctFunc() {
        Map<Integer, String> words = new HashMap<>();
        words.put(1, "hello");
        words.put(2, "reactive");
        words.put(3, "world");
        List<Integer> numbers = Arrays.asList(1, 2, 2, 3);
        Observable.from(numbers)
                  .distinct(words::get)
                  .map(words::get)
                  .subscribe(System.out::println);
    }

}
