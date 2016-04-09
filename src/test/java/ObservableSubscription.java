import org.junit.Test;
import rx.Observable;
import rx.Subscription;

import java.util.concurrent.TimeUnit;


/**
 * @author Pablo Perez
 */
public class ObservableSubscription {

    private String foo="empty";

    int total = 0;

    /**
     * In this test we prove how when we subscribe a observable, this one block the thread until emmitt all items
     */
    @Test
    public void testObservableSubscriptionBlockMainThread() {
        Integer[] numbers = {0, 1, 2, 3, 4};

        Observable.from(numbers)
                  .flatMap(Observable::just)
                  .doOnNext(number->{
                      try {
                          Thread.sleep(100);
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }
                  })
                  .subscribe(number -> total+=number);
        System.out.println("I finish after all items are emmitted:"+total);
    }

    @Test
    public void testObservable() {
        Subscription subscription = Observable.just(1)
                                              .delay(5, TimeUnit.MILLISECONDS)
                                              .subscribe(number -> foo = "Subscription finish");
        while (!subscription.isUnsubscribed()) {
            System.out.println("wait for subscription to finish");
        }
        System.out.println(foo);
    }
}
