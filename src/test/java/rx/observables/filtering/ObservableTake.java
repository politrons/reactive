package rx.observables.filtering;

import org.junit.Test;
import rx.Observable;

import java.util.Arrays;
import java.util.List;


/**
 * Take operator will take items to be emitted by the observable to the observer under some circumstances.
 */
public class ObservableTake {

    /**
     * We take constantClass number of items to be emitted to the observer
     * Shall print
     * 1,2,3,4
     */
    @Test
    public void testTake() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Observable.from(numbers)
                  .take(4)
                  .subscribe(System.out::println);
    }

    /**
     * We take the first item that satisfy the predicate function, and we emit only that value skiping the previous and next items
     * Shall print
     * 4,5
     */
    @Test
    public void testTakeFirst() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Observable.from(numbers)
                  .takeFirst(number -> number > 3)
                  .subscribe(System.out::println);
    }

    /**
     * We take the last number of items specified to be emitted to the observer
     * Shall print
     * 4,5
     */
    @Test
    public void testTakeLast() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Observable.from(numbers)
                  .takeLast(2)
                  .subscribe(System.out::println);
    }

    /**
     * We take the emit of items while the predicate function is true
     * Shall print
     * 1,2,3
     */
    @Test
    public void tesTakeWhile() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Observable.from(numbers)
                  .takeWhile(number -> number < 4)
                  .subscribe(System.out::println);

    }

    /**
     * We take the emit of items while the predicate function is true
     * Shall print
     * 1,2,3
     */
    @Test
    public void tesTakeUntil() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Observable.from(numbers)
                  .takeUntil(number -> number > 3)
                  .subscribe(System.out::println);

    }

    @Test
    public void getFirstUser(){
        Observable<List<Integer>> findUser =Observable.just(Arrays.asList(1,2,3));
        Observable<Integer> user = findUser
                .flatMap(Observable::from)
                .first();
        user.subscribe(System.out::println);
    }
}
