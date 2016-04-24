package relay;

import com.jakewharton.rxrelay.BehaviorRelay;
import org.junit.Test;
import rx.Subscriber;


/**
 * @author Pablo Perez
 */
public class CreatingRelay {



    @Test
    public void testRelay() {


        // observer will receive all events.
        BehaviorRelay<Object> relay = BehaviorRelay.create("default");
        relay.subscribe(getSubscriber());
        relay.call("1");
        relay.call("2");
        relay.call("3");
        relay.subscribe(getSubscriber());
        relay.call("4");
        relay.call("5");
    }

    private Subscriber getSubscriber() {
        return new Subscriber() {
            @Override
            public void onCompleted() {
                System.out.println("done");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e);
            }

            @Override
            public void onNext(Object o) {
                System.out.println(o);

            }
        };
    }


}
