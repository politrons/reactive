import org.junit.Test;
import rx.Observable;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


public class HotObservable<T> {


    /**
     * In this example we see how using hot observables ConnectableObservables we can start emitting items not when we subscribe, but when we connect.
     * @throws InterruptedException
     */
    @Test
    public void testHotObservableConnectableObservables() throws InterruptedException {
        Long startTime = System.currentTimeMillis();
        Observable<String> observable = Observable.just("Hot observable");
        ConnectableObservable<String> connectableObservable = observable.publish();
        connectableObservable.subscribe(s -> System.out.println(String.format("Item %s Emitted after: %s seconds", s, (System.currentTimeMillis() - startTime)/1000)),
                                        e -> System.out.println(e.getMessage()));
        Thread.sleep(1000);
        connectableObservable.connect();
    }


    /**
     * In this example we see how using hot observables PublishSubject we can start emitting items not when we subscribe,
     * but when we subscribe the observer to the observable.
     * @throws InterruptedException
     */
    @Test
    public void testHotObservablePublishSubject() throws InterruptedException {
        Long startTime = System.currentTimeMillis();
        Observable<String> observable = Observable.just("Hot observable");
        PublishSubject publishSubject = PublishSubject.create();
        publishSubject.subscribe(s -> System.out.println(String.format("Item %s Emitted in publish subject after: %s seconds", s,
                                                                       (System.currentTimeMillis() - startTime)/1000)));
        Thread.sleep(1000);
        observable.subscribe(publishSubject);

    }


    /**
     * In this example we see how using hot observables PublishSubject we can start emitting items not when we subscribe,
     * but when we subscribe the observer to the observable.
     * @throws InterruptedException
     */
    @Test
    public void testHotObservableReplay() throws InterruptedException {
        Long startTime = System.currentTimeMillis();
        ConnectableObservable<Integer> replay =  Observable.range(0, 10).publish();
        replay.subscribe(s -> System.out.println(String.format("Item %s Emitted ", s)));
        replay.connect();
        Observable<Integer> observable = replay.subscribeOn(Schedulers.io()).count();
        observable.toBlocking().forEach(item-> System.out.println("Item replayed after being emitted by ConnectableObservable " + item));
    }



}
