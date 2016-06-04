package rx.observables;

import org.junit.Test;
import rx.Observable;

import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertTrue;


/**
 * @author Pablo Perez
 */
public class BasicsObservable {



    /**
     * We create an observable through a subscriber where we define the item emitted in every onNext, and once we finish to emmit we set onComplete,
     * Which make the subscriber to finish.
     */
    @Test
    public void createObservable() {

        Observable.create(subscriber -> {
            for (int n : getNumbers()) {
                System.out.println("generating");
                subscriber.onNext(n);
            }
            subscriber.onCompleted();
        })
                  .subscribe(n -> System.out.println("item: " + n), (error) -> System.out.println("Something went wrong" + error.getMessage()),
                             () -> System.out.println("This observable has finished"));
    }

    public int[] getNumbers() {
        return new int[]{0, 1, 2, 3, 4, 5};
    }


    @Test
    public void testObservable() {
        Integer[] numbers = {0, 1, 2, 3, 4, 5};

        Observable.from(numbers)
                  .subscribe((incomingNumber) -> System.out.println("incomingNumber " + incomingNumber),
                             (error) -> System.out.println("Something went wrong" + error.getMessage()),
                             () -> System.out.println("This observable is finished"));
    }

    /**
     * Create an observable with a Long value evey second
     */
    @Test
    public void intervalObservable() {
        Observable.interval(1, TimeUnit.SECONDS)
                  .map(Long::new)
                  .subscribe(result -> System.out.printf(String.valueOf(result)));
    }

    /**
     * Create an observable with a Long value evey second
     */
    @Test
    public void observableEvolveAndToValue() {
        assertTrue(Observable.just(10)
                             .map(String::valueOf)
                             .toBlocking()
                             .single()
                             .equals("10"));
    }


}
