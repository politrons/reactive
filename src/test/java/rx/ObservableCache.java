package rx;

import org.junit.Test;


/**
 * @author Pablo Perez
 */
public class ObservableCache {


    /**
     * Here we can prove how the first time the items are delayed 100 ms per item emitted but second time becuase it´s cached we dont have any delay since
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
