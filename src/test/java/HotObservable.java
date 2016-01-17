import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;


public class HotObservable<T> {


    /**
     * This example we can see how a third observable subscribe to hot Observable once start emitting items so he miss the first items
     *
     * @throws InterruptedException
     */
    @Test
    public void testHotObservables() throws InterruptedException {
        Observable<Long> interval = Observable.interval(100L, TimeUnit.MILLISECONDS);
        ConnectableObservable<Long> published = interval.publish();
        Subscription sub1 = subscribePrint(published, "First");
        Subscription sub2 = subscribePrint(published, "Second");
        published.connect();
        try {
            Thread.sleep(500L);
            Subscription sub3 = subscribePrint(published, "Third");
            Thread.sleep(500L);
            sub1.unsubscribe();
            sub2.unsubscribe();
            sub3.unsubscribe();
        } catch (InterruptedException e) {
        }
    }

    Subscription subscribePrint(Observable<Long> observable, String name) {
        return observable.subscribe((v) -> System.out.println(name + " : " + v), (e) -> {
            System.err.println("Error from " + name + ":");
            System.err.println(e.getMessage());
        }, () -> System.out.println(name + " ended!"));
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
        ConnectableObservable<String> connectableObservable = observable.publish();
        connectableObservable.subscribe(
                s -> System.out.println(String.format("Item %s Emitted after: %s seconds", s, (System.currentTimeMillis() - startTime) / 1000)),
                e -> System.out.println(e.getMessage()));
        Thread.sleep(1000);
        connectableObservable.connect();
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
     * In this example we see how using hot observables PublishSubject we can start emitting items not when we subscribe,
     * but when we subscribe the observer to the observable.
     *
     * @throws InterruptedException
     */
    @Test
    public void testHotObservableReplay() throws InterruptedException {
        ConnectableObservable<Integer> replay = Observable.range(0, 10)
                                                          .publish();
        replay.subscribe(s -> System.out.println(String.format("Item %s Emitted ", s)));
        replay.connect();
        Observable<Integer> observable = replay.subscribeOn(Schedulers.io())
                                               .count();
        observable.toBlocking()
                  .forEach(item -> System.out.println("Item replayed after being emitted by ConnectableObservable " + item));
    }


}
