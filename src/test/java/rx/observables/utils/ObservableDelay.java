package rx.observables.utils;

import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.observers.TestSubscriber;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


/**
 * Delay operator will delay transformation of our Observable from lazy to eager the time that you specify
 */
public class ObservableDelay {

    /**
     * Using the delay operator we delay the creation of the pipeline from lazy to eager.
     * But once start emitting the delay operator does not affect the items emitted
     */
    @Test
    public void delayCreation() {
        long start = System.currentTimeMillis();
        Subscription subscription = Observable.just("hello reactive world")
                .delay(200, TimeUnit.MICROSECONDS)
                .subscribe(n -> System.out.println("time:" + (System.currentTimeMillis() - start)));
        new TestSubscriber((Observer) subscription).awaitTerminalEvent(1000, TimeUnit.MILLISECONDS);
    }

    /**
     * If we want to delay the every single item emitted in the pipeline we will need a hack,
     * one possible hack is use zip operator and combine every item emitted with an interval so every item emitted has to wait until interval emit the item.
     * Shall print
     * <p>
     * time:586
     * time:783
     * time:982
     */
    @Test
    public void delaySteps() {
        long start = System.currentTimeMillis();
        Subscription subscription =
                Observable.zip(Observable.from(Arrays.asList(1, 2, 3)), Observable.interval(200, TimeUnit.MILLISECONDS),
                               (i, t) -> i)
                        .subscribe(n -> System.out.println("time:" + (System.currentTimeMillis() - start)));
        new TestSubscriber((Observer) subscription).awaitTerminalEvent(3000, TimeUnit.MILLISECONDS);
    }
}
