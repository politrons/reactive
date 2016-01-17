import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.observables.ConnectableObservable;
import rx.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;


public class HotObservable {


    /**
     * This example we can see how a third observable subscribe to hot Observable once this one haa start emitting items, so he miss the items already emitted
     *
     * @throws InterruptedException
     */
    @Test
    public void testHotObservablesMissingItems() throws InterruptedException {
        Observable<Long> interval = Observable.interval(100L, TimeUnit.MILLISECONDS);
        ConnectableObservable<Long> published = interval.publish();
        Subscription sub1 = subscribePrint(published, "First");
        Subscription sub2 = subscribePrint(published, "Second");
        published.connect();
        thirdSubscriber(published, sub1, sub2);
    }

    /**
     * This example we can see how a third observable subscribe to hot Observable once start emitting items, and because the hot
     * observable was created with replay, it replay to the third subscriber all missed items.
     *
     * @throws InterruptedException
     */
    @Test
    public void testHotObservablesRecoveringMissItems() throws InterruptedException {
        Observable<Long> interval = Observable.interval(100L, TimeUnit.MILLISECONDS);
        ConnectableObservable<Long> published = interval.replay();
        Subscription sub1 = subscribePrint(published, "First");
        Subscription sub2 = subscribePrint(published, "Second");
        published.connect();
        thirdSubscriber(published, sub1, sub2);
    }


    private void thirdSubscriber(ConnectableObservable<Long> published, Subscription sub1, Subscription sub2) {
        try {
            Thread.sleep(500L);
            Subscription sub3 = subscribePrint(published, "Third");
            Thread.sleep(500L);
            unsubscribe(sub1, sub2, sub3);
        } catch (InterruptedException e) {
        }
    }

    private void unsubscribe(Subscription sub1, Subscription sub2, Subscription sub3) {
        sub1.unsubscribe();
        sub2.unsubscribe();
        sub3.unsubscribe();
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

}
