package rx.observables.filtering;

import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.observers.TestSubscriber;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Skip operator will skip items to be emitted by the observable to the observer under some circumstances.
 */
public class ObservableSkip {

    /**
     * We skip a number of items to be emitted to the observer
     * Shall print
     * 5
     */
    @Test
    public void testSkip() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Observable.from(numbers)
                  .skip(4)
                  .subscribe(System.out::println);
    }

    /**
     * We skip the last number of items specified to be emitted to the observer
     * Shall print
     * 1,2,3
     */
    @Test
    public void testSkipLast() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Observable.from(numbers)
                  .skipLast(2)
                  .subscribe(System.out::println);
    }

    /**
     * We skip the emit of items while the predicate function is true
     * Shall print
     * 4,5
     */
    @Test
    public void testSkipWhile() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Observable.from(numbers)
                  .skipWhile(number -> number < 4)
                  .subscribe(System.out::println);

    }

    /**
     * We skip the emit of items until the passed observable start emitting items
     * Shall print
     * 4,5
     */
    @Test
    public void testSkiUitil() throws InterruptedException {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Observable observable2 = Observable.just(1);
        Subscription subscription = Observable.from(numbers)
                                              .skipUntil(observable2)
                                              .subscribe(System.out::println);
        Thread.sleep(3000);
        observable2.subscribe();
        new TestSubscriber((Observer) subscription).awaitTerminalEvent(5, TimeUnit.SECONDS);

    }

}
