import org.junit.Test;
import rx.Observable;
import rx.Subscription;

import java.util.concurrent.TimeUnit;


/**
 * @author Pablo Perez
 */
public class ObservableSubscription {

    private String foo="empty";

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
