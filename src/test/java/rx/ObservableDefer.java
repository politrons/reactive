package rx;

import org.junit.Test;
import rx.Observable;

import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * @author Pablo Perez
 */
public class ObservableDefer {


    /**
     * Here we see how after get the observable and get the first emmited element, we´re block until observable is subscribe
     */
    @Test
    public void testObservableWaitForSubscription() {
        Random random = new Random(10);
        int[] x = random.ints(100)
                        .toArray();
        Integer[] numbers = new Integer[x.length];
        int index = 0;
        for (int number : x) {
            numbers[index++] = number;
        }
        Observable<String> observable = Observable.defer(() -> Observable.just("The first observable wait for the subscription to finish")
                                                                         .doOnNext(b -> Observable.from(numbers)
                                                                                                  .subscribe(i -> System.out.println(
                                                                                                          "This observable has finished:" + i))));
        System.out.println("Observable returned");
        System.out.println(observable.toBlocking()
                                     .first());
    }

    /**
     * Here we see how after get the observable and get the first emitted element, we´re not block until observable is subscribe because is executed in
     * scheduler
     */
    @Test
    public void testObservableNotWaitForSubscription() {
        Random random = new Random(10);
        int[] x = random.ints(100)
                        .toArray();
        Integer[] numbers = new Integer[x.length];
        int index = 0;
        for (int number : x) {
            numbers[index++] = number;
        }
        Observable<String> observable = Observable.defer(() -> Observable.just("The first observable wait for the subscription to finish")
                                                                         .doOnNext(b -> Observable.interval(1, TimeUnit.SECONDS)
                                                                                                  .subscribe(i -> System.out.println(
                                                                                                          "This observable has finished:" + i))));
        System.out.println("Observable returned");
        System.out.println(observable.toBlocking()
                                     .first());
    }


}