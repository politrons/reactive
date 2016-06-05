package rx.observables;

import org.junit.Test;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * @author Pablo Perez
 *         Using the option subscribeOn or observerOn, you specify in your observable that you want to execute all/some pipeline steps into another thread,
 *         making the pipeline asyncronious
 */
public class ObservableAsynchronous {

    int total = 0;

    Integer[] numbers = {0, 1, 2, 3, 4};

    //************************DIFFERENCE BETWEEN subscribeOn AND observerOn***********************\
    /**
     * Once that you set in your pipeline the observerOn all the next steps of your pipeline will be executed in another thread.
     * Shall print
     *      First step main
     *      Second step RxNewThreadScheduler-1
     */
    @Test
    public void testObservableObserverOn() throws InterruptedException {
        Subscription subscription = Observable.just(1)
                                              .doOnNext(number -> System.out.println("First step " + Thread.currentThread()
                                                                                                                    .getName()))
                                              .observeOn(Schedulers.newThread())
                                              .doOnNext(number -> System.out.println( "Second step " + Thread.currentThread()
                                                                                                                    .getName()))
                                              .subscribe();
        while (!subscription.isUnsubscribed()) {
            Thread.sleep(100);
        }
    }

    /**
     * Does not matter at what point in your pipeline you set your subscribeOn, once that is set in the pipeline,
     * all steps will be executed in another thread.
     * Shall print
     *      First step RxNewThreadScheduler-1
     *      Second step RxNewThreadScheduler-1
     */
    @Test
    public void testObservableSubscribeOn() throws InterruptedException {
        Subscription subscription = Observable.just(1)
                                              .doOnNext(number -> System.out.println("First step " + Thread.currentThread()
                                                                                                                    .getName()))
                                              .subscribeOn(Schedulers.newThread())
                                              .doOnNext(number -> System.out.println("Second step " + Thread.currentThread()
                                                                                                                    .getName()))
                                              .subscribe();
        while (!subscription.isUnsubscribed()) {
            Thread.sleep(100);
        }
    }


    //****************DIFFERENCE BETWEEN ASYNC AND SYNC OBSERVABLE*********************\\

    /**
     * In this test we prove how when we subscribe a observable using scheduler, this one is executed in another thread,
     * and total is in the scope of every thread.
     */
    @Test
    public void testObservableAsync() throws InterruptedException {
        Subscription subscription = Observable.from(numbers)
                                              .doOnNext(increaseTotalItemsEmitted())
                                              .subscribeOn(Schedulers.newThread())
                                              .subscribe(number -> System.out.println("Items emitted:" + total));
        System.out.println("I finish before the observable finish.  Items emitted:" + total);
        while (!subscription.isUnsubscribed()) {
            Thread.sleep(100);
        }
    }

    /**
     * In this test we prove how when we subscribe a observable ans we not use subscribeOn, this one is executed in the main thread.
     * And total is in the scope of both
     */
    @Test
    public void testObservableSync() {
        Observable.from(numbers)
                  .doOnNext(increaseTotalItemsEmitted())
                  .subscribe(number -> System.out.println("Items emitted:" + total));

        System.out.println("I finish after the observable finish.  Items emitted:" + total);
    }


    private Action1<Integer> increaseTotalItemsEmitted() {
        return number -> {
            try {
                total += number;
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }


}
