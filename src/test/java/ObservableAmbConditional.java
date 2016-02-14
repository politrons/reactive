import org.junit.Test;
import rx.Observable;
import rx.Subscription;

import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * @author Pablo Perez
 */
public class ObservableAmbConditional {


    /**
     * Amb condional only takes into account the first observable that start emitting, the second one will be avoided completely
     */
    @Test
    public void ambObservable() {
        Observable<String> words = Observable.just("Some", "Other");
        Observable<Long> interval = Observable
                .interval(500L, TimeUnit.MILLISECONDS)
                .take(2);
        subscribePrint(Observable.amb(words, interval), "Amb 1");
    }

    /**
     * In here it will depend which of the data source start before
     */
    @Test
    public void ambObservableRandom() {
        Random r = new Random();
        Observable<String> source1 = Observable
                .just("data from source 1")
                .delay(r.nextInt(1000), TimeUnit.MILLISECONDS);
        Observable<String> source2 = Observable
                .just("data from source 2")
                .delay(r.nextInt(1000), TimeUnit.MILLISECONDS);
        subscribePrint(Observable.amb(source1, source2), "Amb 2");
    }

    Subscription subscribePrint(Observable<Serializable> observable, String name) {
        return observable.subscribe((v) -> System.out.println(name + " : " + v), (e) -> {
            System.err.println("Error from " + name + ":");
            System.err.println(e.getMessage());
        }, () -> System.out.println(name + " ended!"));
    }


}
