package rx.observables.creating;

import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscription;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.subjects.AsyncSubject;

import java.util.Arrays;
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
        System.out.println("Vertx started");
    }

    @Test
    public void testIntervalAsyncSubject() {
        Scheduler scheduler = Schedulers.newThread();
        AsyncSubject<String> asyncSubject = AsyncSubject.create();
        final Subscription subscribe = Observable.interval(50, TimeUnit.MILLISECONDS, scheduler)
                .map(time -> "item emitted\n")
                .doOnCompleted(() -> System.out.println("all items emitted"))
                .subscribe(System.out::println);
        asyncSubject.subscribeOn(scheduler).subscribe((Observer<? super String>) subscribe);
        new TestSubscriber().awaitTerminalEvent(200, TimeUnit.MILLISECONDS);
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

    /**
     * Interval can be used with zipWith which will allow you to zip one element of your observable emission with the next
     * after N interval time.
     */
    @Test
    public void intervalWithZip() {
        Observable.interval(500, TimeUnit.MILLISECONDS)
                .zipWith(Observable.from(Arrays.asList(1, 2, 3, 4, 5)), (a, b) -> String.valueOf(a).concat("-").concat(String.valueOf(b)))
                .subscribe(number -> System.out.println("number:" + number),
                        System.out::println,
                        System.out::println);
        TestSubscriber testSubscriber = new TestSubscriber();
        testSubscriber.awaitTerminalEvent(5000, TimeUnit.MILLISECONDS);
    }
    @Test
    public void testIntervalObservableWithError() {
        Observable.interval(100, TimeUnit.MILLISECONDS)
                .map(time -> "item\n")
                .map(time -> null)
                .materialize()
                .flatMap(item -> Observable.just(item)
                        .map(Object::toString)
                        .onErrorResumeNext(t -> Observable.just("Item error")))
                .subscribe(System.out::print);
        TestSubscriber testSubscriber = new TestSubscriber();
        testSubscriber.awaitTerminalEvent(5000, TimeUnit.MILLISECONDS);
    }

}
