package rx;

import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * Created by pabloperezgarcia on 10/3/16.
 */
public class ObservableInterval {

    @Test
    public void testIntervalObservable() {
        Observable.interval(1, TimeUnit.SECONDS, Schedulers.io())
                  .map(event -> {
                      System.out.print("event:" + event);
                      return event;
                  })
                  .subscribe(event -> System.out.print("final event:" + event));
    }

}
