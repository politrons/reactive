package rx.observables.connectable;

import akka.dispatch.ExecutionContexts;
import akka.dispatch.Futures;
import akka.dispatch.OnComplete;
import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;
import rx.subjects.AsyncSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;
import scala.Function1;
import scala.concurrent.ExecutionContextExecutorService;
import scala.concurrent.Future;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;
import scala.util.Try;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.*;


/**
 * Hot Observables are Observables that allow start emitting when we connect to the publisher
 * or features as retry items emitted through the pipeline to new observers subscribed.
 */
public class HotObservable {


    /**
     * This example we can see how a third observer subscribe to hot Observable once this one has start emitting items,
     * Since the hot observable was created with publish he miss the items already emitted
     *
     * @throws InterruptedException
     */
    @Test
    public void testHotObservablesMissingItems() throws InterruptedException {
        Observable<Long> interval = Observable.interval(100L, TimeUnit.MILLISECONDS);
        ConnectableObservable<Long> published = interval.publish();
        subscribeToObservable(published, "First");
        subscribeToObservable(published, "Second");
        published.connect();
        subscribeToObservableWithDelay(published);

    }

    /**
     * This example we can see how a third observer subscribe to hot Observable once start emitting items, and because the hot
     * observable was created with replay, it replay to the third observer all missed items.
     *
     * @throws InterruptedException
     */
    @Test
    public void testHotObservablesReplayingMissItems() throws InterruptedException {
        Observable<Long> interval = Observable.interval(100L, TimeUnit.MILLISECONDS);
        ConnectableObservable<Long> published = interval.replay();
        subscribeToObservable(published, "First");
        subscribeToObservable(published, "Second");
        published.connect();
        subscribeToObservableWithDelay(published);
    }

    /**
     * In this example we see how using hot observables PublishSubject we can emit an item on broadcast to all the observers(subscribers).
     *
     * @throws InterruptedException
     */
    @Test
    public void testHotObservableUsingPublishSubject() throws InterruptedException {
        Observable<Long> interval = Observable.interval(100L, TimeUnit.MILLISECONDS);
        Subject<Long, Long> publishSubject = PublishSubject.create();
        interval.subscribe(publishSubject);
        subscribeToObservable(publishSubject, "First");
        subscribeToObservable(publishSubject, "Second");
        try {
            Thread.sleep(300L);
            publishSubject.onNext(555L);
            subscribeToObservable(publishSubject, "Third");
            Thread.sleep(500L);
        } catch (InterruptedException e) {
        }
    }

    /**
     * In this example we see how using hot observables ConnectableObservables we can start emitting items not when we subscribe, but when we connect.
     *
     * @throws InterruptedException
     */
    @Test
    public void testHotObservableConnectableObservables() throws InterruptedException {
        Long startTime = System.currentTimeMillis();
        Observable<String> observable = Observable.just("Hot observable");
        ConnectableObservable<String> published = observable.publish();
        published.subscribe(s -> System.out.println(String.format("Item %s Emitted after: %s seconds", s, (System.currentTimeMillis() - startTime) / 1000)),
                e -> System.out.println(e.getMessage()));
        Thread.sleep(1000);
        published.connect();
    }

    /**
     * In this example we see how using hot observables PublishSubject we can start emitting items not when we subscribe,
     * but when we subscribe the observer to the observable.
     *
     * @throws InterruptedException
     */
    @Test
    public void testHotObservablePublishSubject() throws InterruptedException {
        Long startTime = System.currentTimeMillis();
        Observable<String> observable = Observable.just("Hot observable");
        PublishSubject<String> publishSubject = PublishSubject.create();
        publishSubject.subscribe(s -> System.out.println(
                String.format("Item %s Emitted in publish subject after: %s seconds", s, (System.currentTimeMillis() - startTime) / 1000)));
        Thread.sleep(1000);
        observable.subscribe(publishSubject);

    }

    /**
     * In this example we see how using hot observables ReplaySubject we can emit an item on broadcast to all the observers(subscribers).
     *
     * @throws InterruptedException
     */
    @Test
    public void testHotObservableUsingAsyncSubject() throws InterruptedException {
        Observable<Long> interval = Observable.interval(100L, TimeUnit.MILLISECONDS);
        Subject<Long, Long> publishSubject = AsyncSubject.create();
        interval.subscribe(publishSubject);
        Thread.sleep(1000L);
        subscribeToObservable(publishSubject, "First");
        subscribeToObservable(publishSubject, "Second");
        subscribeToObservable(publishSubject, "Third");
    }


    /**
     * In this example we see how using hot observables ReplaySubject we can emit an item on broadcast to all the observers(subscribers).
     *
     * @throws InterruptedException
     */
    @Test
    public void testHotObservableUsingReplaySubject() throws InterruptedException {
        Observable<Long> interval = Observable.interval(100L, TimeUnit.MILLISECONDS);
        Subject<Long, Long> publishSubject = ReplaySubject.create(1);
        interval.subscribe(publishSubject);
        Thread.sleep(1000L);
        subscribeToObservable(publishSubject, "First");
        subscribeToObservable(publishSubject, "Second");
        subscribeToObservable(publishSubject, "Third");
    }

    /**
     * In this example we see how using hot observables ReplaySubject we can emit an item on broadcast to all the observers(subscribers).
     *
     * @throws InterruptedException
     */
    @Test
    public void testHotObservableUsingReplaySubject2() throws InterruptedException {
        Observable<Long> interval = Observable.interval(100L, TimeUnit.MILLISECONDS);
        Subject<Long, Long> publishSubject = ReplaySubject.create(1);
        interval.subscribe(publishSubject);
        Thread.sleep(1000L);
        publishSubject.subscribe(System.out::println, (e) -> System.err.println(e.getMessage()), System.out::println);
    }

    private Subscription subscribeToObservableWithDelay(ConnectableObservable<Long> published) {
        Subscription sub3 = null;
        try {
            Thread.sleep(500L);
            sub3 = subscribeToObservable(published, "Third");
            Thread.sleep(500L);
        } catch (InterruptedException e) {
        }
        return sub3;
    }


    Subscription subscribeToObservable(Observable<Long> observable, String name) {
        return observable.subscribe((v) -> System.out.println(name + " : " + v), (e) -> {
            System.err.println("Error from " + name + ":");
            System.err.println(e.getMessage());
        }, () -> System.out.println(name + " ended!"));
    }


    /**
     * In this example we see how using hot observables ReplaySubject we can emit an item on broadcast to all the observers(subscribers).
     *
     * @throws InterruptedException
     */
    @Test
    public void chainObservables() throws InterruptedException {
        Observable<Long> interval = Observable.just(1l);
        Subject<Long, Long> publishSubject = ReplaySubject.create(1);
        Subject<Long, Long> publishSubject2 = ReplaySubject.create(1);
        interval.subscribe(publishSubject);
        publishSubject.subscribe(publishSubject2);
        publishSubject2.subscribe(System.out::println, (e) -> System.err.println(e.getMessage()), System.out::println);
    }

    @Test
    public void createObservableFromFuture() throws InterruptedException {
        Optional<Object> o2 = Optional.of(null);
        Observable<String> observableFuture = getObservableFuture();
        Observable<String> observableFuture1 = getObservableFuture();
        Observable.zip(observableFuture, observableFuture1, (o, o1) -> o.concat(":").concat(o1))
                .subscribe(value -> System.out.println("Result:" + value),
                        System.out::println, () -> System.out.println("end"));
    }

    private Observable<String> getObservableFuture() {
        ExecutionContextExecutorService executionContextExecutorService = ExecutionContexts.fromExecutorService(newCachedThreadPool());
        ReplaySubject<String> publishSubject = ReplaySubject.create(1);
        Future<String> future = Futures.<String>promise().success("works").future();
        future.onComplete(getFunction(publishSubject), executionContextExecutorService);
        return publishSubject.first().single();
    }

    private OnComplete getFunction(ReplaySubject<String> publishSubject) {
        return new OnComplete() {
            @Override
            public void onComplete(Throwable failure, Object success) throws Throwable {
                Observable<String> observable = Observable.just((String) success);
                observable.subscribe(publishSubject);
            }
        };
    }

}
