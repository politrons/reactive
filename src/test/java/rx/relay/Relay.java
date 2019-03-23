package rx.relay;

import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.ReplayRelay;
import org.junit.Test;
import rx.Observer;
import rx.Subscription;


/**
 * @author Pablo Perez
 * Relay is just constantClass type of observable which only implement the onNext function. It means that never invoke onComplete so never unsubscribe the observers.
 */
public class Relay {


    /**
     * Relay is just an observable which subscribe an observer, but it wont unsubscribe once emit the items. So the pipeline keep open
     * It should return 1,2,3,4,5 for first observer and just 3, 4, 5 fot the second observer since default relay emit last emitted item,
     * and all the next items passed to the pipeline.
     */
    @Test
    public void testRelay() throws InterruptedException {
        BehaviorRelay<String> relay = BehaviorRelay.create("default");
        relay.subscribe(result -> System.out.println("Observer1:" + result));
        relay.call("1");
        relay.call("2");
        relay.call("3");
        relay.subscribe(result -> System.out.println("Observer2:" + result));
        relay.call("4");
        relay.call("5");
    }


    /**
     * ReplayRelay it works just like hot observables, once that an observer subscribe, the Relay will replay all items already emitted
     * to another observer.
     * it should return 1,2,3,4,5 for both observers.
     */
    @Test
    public void testReplayRelay() {
        ReplayRelay<String> relay = ReplayRelay.create();
        relay.subscribe(result -> System.out.println("Observer1:" + result));
        relay.call("1");
        relay.call("2");
        relay.call("3");
        relay.subscribe(result -> System.out.println("Observer2:" + result));
        relay.call("4");
        relay.call("5");
    }

    /**
     * ReplayRelay it works just like hot observables, once that an observer subscribe, the Relay will replay all items already emitted
     * to another observer.
     * it should return 1,2,3,4,5 for both observers.
     */
    @Test
    public void testReplayRelaySharingObserver() {
        ReplayRelay<String> relay = ReplayRelay.create();
        Subscription subscription = relay.subscribe(result -> System.out.println("Observer1:" + result));
        relay.call("1");
        relay.call("2");
        relay.call("3");
        relay.subscribe((Observer<? super String>) subscription);
        relay.call("4");
        relay.call("5");
    }

}
