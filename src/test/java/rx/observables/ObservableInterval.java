package rx.observables;

import org.junit.Test;
import rx.Observable;

import java.util.concurrent.TimeUnit;

/**
 * Created by pabloperezgarcia on 10/3/16.
 * An Observable that returns the items every interval time specify
 */
public class ObservableInterval {

    @Test
    public void testIntervalObservable() {
        Observable.interval(1, TimeUnit.SECONDS)
                  .map(time-> "item emitted")
                  .subscribe(System.out::print,
                             item -> System.out.print("final:" + item));
    }

}
