package rx.relay;

import com.jakewharton.rxrelay.BehaviorRelay;
import org.junit.Test;


/**
 * @author Pablo Perez
 */
public class Relay {


    /**
     * Relay is just a subject which subscribe an observer, but it wont unsubscribe once emit the item. So the pipeline keep open
     */
    @Test
    public void testRelay() {
        BehaviorRelay<Object> relay = BehaviorRelay.create("default");
        relay.subscribe(result -> System.out.println("Observer1:" + result));
        relay.call("1");
        relay.call("2");
        relay.call("3");
        relay.subscribe(result -> System.out.println("Observer2:" + result));
        relay.call("4");
        relay.call("5");
    }
}
