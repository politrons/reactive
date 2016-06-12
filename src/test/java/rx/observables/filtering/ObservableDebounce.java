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
 * Debounce operator only emit items if the last item emitted by observable before the timeout, then restart the timer and start again.
 */
public class ObservableDebounce {

    @Test
    public void testDistinct() {
        List<Integer> numbers = Arrays.asList(5, 10, 15, 5, 10, 15, 40, 10, 50, 10);
        Subscription subscription = Observable.from(numbers)
                                              .map(number -> {
                                                  sleep(number);
                                                  return number;
                                              })
                                              .debounce(30, TimeUnit.MILLISECONDS)
                                              .subscribe(System.out::println);

        new TestSubscriber((Observer) subscription).awaitTerminalEvent(1, TimeUnit.SECONDS);
    }

    private void sleep(Integer number) throws RuntimeException {
        try {
            Thread.sleep(number);
        } catch (InterruptedException e) {
            new RuntimeException();
        }
    }


}
