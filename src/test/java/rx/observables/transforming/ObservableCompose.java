package rx.observables.transforming;

import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Using compose we can get an observable and transform into another type or value.
 * We pass to compose a Transformer function that get the observable and change his type or value
 */
public class ObservableCompose {


    private Scheduler mainThread = Schedulers.newThread();


    Observable.Transformer<Integer, Integer> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                                       .observeOn(mainThread);
    }

    Observable.Transformer<Integer, String> transformIntegerToString() {
        return observable -> observable.map(String::valueOf);
    }


    /**
     * In this example we apply in the observable that all actions before the compose are executed in another thread.
     * But the result in the observer is processed in the main thread.
     */
    @Test
    public void observableWithScheduler() throws InterruptedException {
        Observable.just(1)
                  .map(number -> {
                      System.out.println("Item processed on thread:" + Thread.currentThread()
                                                                             .getName());
                      return number;
                  })
                  .compose(applySchedulers())
                  .subscribe(number -> System.out.println("Result in thread:" + Thread.currentThread()
                                                                                      .getName()));
        Thread.sleep(1000);
    }

    /**
     * In this example we use a transformer to get the Integer item emitted and transform to String
     */
    @Test
    public void observableWithTransformToString() {
        Observable.just(1)
                  .map(number -> {
                      System.out.println("Item is Integer:" + Integer.class.isInstance(number));
                      return number;
                  })
                  .compose(transformIntegerToString())
                  .subscribe(number -> System.out.println("Item is String:" + (String.class.isInstance(number))));

    }


}
