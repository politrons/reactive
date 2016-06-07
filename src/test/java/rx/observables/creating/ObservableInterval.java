package rx.observables.creating;

import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.observers.TestSubscriber;

import java.util.concurrent.TimeUnit;

/**
 * Created by pabloperezgarcia on 10/3/16.
 * <p>
 * An Observable that emit an item every interval time specify.
 */
public class ObservableInterval {

    /**
     * Since interval work asynchronously you will have to use TestSubscriber class to wait a period of time
     * to see some items emitted. This type of observable never finish to emit, in order to stop, you will need unsubscribe the observer.
     */
    @Test
    public void testIntervalObservable() {
        Subscription subscription = Observable.interval(50, TimeUnit.MILLISECONDS)
                                              .map(time -> "item emitted\n")
                                              .subscribe(System.out::print);
        new TestSubscriber((Observer) subscription).awaitTerminalEvent(200, TimeUnit.MILLISECONDS);
    }


    /**
     * Since interval work asynchronously you will have to use TestSubscriber class to wait a period of time
     * to see some items emitted. This type of observable never finish since we dont specify a max number of intervals.
     * Since we unsubscribe after wait 200ms should stop emitting and we should only see 4 items emitted, just like the previous example
     */
    @Test
    public void testIntervalObservableWithMax() {
        Subscription subscription = Observable.interval(50, TimeUnit.MILLISECONDS)
                                              .map(time -> "item emitted\n")
                                              .subscribe(System.out::print);
        TestSubscriber testSubscriber = new TestSubscriber((Observer) subscription);
        testSubscriber.awaitTerminalEvent(200, TimeUnit.MILLISECONDS);
        subscription.unsubscribe();
        testSubscriber.awaitTerminalEvent(200, TimeUnit.MILLISECONDS);
    }
}
