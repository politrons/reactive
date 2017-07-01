package rx.observables.filtering;

import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.observers.TestSubscriber;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by pabloperezgarcia on 10/3/16.
 * <p>
 * An Observable that emit just the first item that pass the predicate condition.
 */
public class ObservableFirst {


    private AtomicInteger atomicInteger = new AtomicInteger();

    /**
     * It´s very interesting use this operator when you have an interval since once that emitt just
     * one item it will unsubscribe the subscriber
     */
    @Test
    public void testFirstInIntervalObservable() {
        Subscription subscription = Observable.interval(50, TimeUnit.MILLISECONDS)
                .doOnNext(item -> System.out.println("Item emitted " + item))
                .first(item -> atomicInteger.incrementAndGet() == 5)
                .subscribe(item -> System.out.println("Condition passed with:" + item));
        new TestSubscriber((Observer) subscription).awaitTerminalEvent(2, TimeUnit.SECONDS);
        System.out.println("is unsubscribed:" + subscription.isUnsubscribed());
    }


}
