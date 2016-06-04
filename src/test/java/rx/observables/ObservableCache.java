package rx.observables;

import org.junit.Test;
import rx.Observable;


/**
 * @author Pablo Perez
 */

/**
 * The feature cache it will cache the last emitted items from the last observer and it will return to the next observer that subscribe to the observable.
 */
public class ObservableCache {


    /**
     * Here we can prove how the first time the items are delayed 100 ms per item emitted but second time because it´s cached we dont have any delay since
     * the item emitted are cached
     */
    @Test
    public void cacheObservable() {
        Integer[] numbers = {0, 1, 2, 3, 4, 5};

        Observable<Integer> observable = Observable.from(numbers)
                                                   .doOnNext(number -> {
                                                       try {
                                                           Thread.sleep(100);
                                                       } catch (InterruptedException e) {
                                                           e.printStackTrace();
                                                       }
                                                   })
                                                   .cache();
        long time = System.currentTimeMillis();
        observable.subscribe(System.out::println);
        System.out.println("First time took:" + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        observable.subscribe(System.out::println);
        System.out.println("Second time took:" + (System.currentTimeMillis() - time));

    }

}
