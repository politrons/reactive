package rx.observables.transforming;

import org.junit.Test;
import rx.Observable;
import rx.Subscription;

import java.util.concurrent.TimeUnit;


/**
 * @author Pablo Perez
 *         /**
 *         Buffer allow keep the observable waiting buffering the items emitted unitl a spceific number of items, or a period of time.
 */
public class ObservableBuffer {


    /**
     * In this example we use buffer(count) which will buffer items until it will take the count number set or end of items.
     * Shall print
     * Group size:3
     * Group size:2
     */
    @Test
    public void bufferCountObservable() {
        Integer[] numbers = {0, 1, 2, 3, 4};
        Observable.from(numbers)
                  .buffer(3)
                  .subscribe(list -> System.out.println("Group size:" + list.size()));

    }

    /**
     * This buffer will wait 50ms after emit the items, since the interval is every 100 ms, we should see a group size of 0, then the next time 1 and so on.
     *
     * @throws InterruptedException
     */
    @Test
    public void bufferTimeStampObservable() throws InterruptedException {
        Subscription subscription = Observable.interval(100, TimeUnit.MILLISECONDS)
                                              .buffer(50, TimeUnit.MILLISECONDS)
                                              .doOnNext(
                                                      list -> System.out.println("Group size " + list.size()))
                                              .subscribe();
        while (!subscription.isUnsubscribed()) {
            Thread.sleep(100);
        }

    }

    @Test
    public void windowCountObservable() {
        Integer[] numbers = {0, 1, 2, 3, 4};

        Observable.from(numbers)
                  .window(3)
                  .flatMap(o-> {
                      System.out.println("New Observable");
                      return o;
                  })
                  .subscribe(number -> System.out.println("Number:" + number));

    }

}
