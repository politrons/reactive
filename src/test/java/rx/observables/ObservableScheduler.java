package rx.observables;

import org.junit.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * @author Pablo Perez
 * Using the option subscribeOn, you specify in your observable that you want to execute all previous pipeline steps into another thread,
 * making the pipeline asyncronious
 */
public class ObservableScheduler {

    int total = 0;

    Integer[] numbers = {0, 1, 2, 3, 4};

    /**
     * In this test we prove how when we subscribe a observable using scheduler, this one is executed in another thread, and they dont share attributes.
     */
    @Test
    public void testObservableSubscriptionAsync() {
        Observable.from(numbers)
                  .doOnNext(increaseTotalItemsEmitted())
                  .subscribeOn(Schedulers.newThread())
                  .subscribe(number -> System.out.println("Items emitted:"+total));

        System.out.println("I finish before the observable finish.  Items emitted:"+total);
    }

    /**
     * In this test we prove how when we subscribe a observable using scheduler, this one is executed in another thread, and they dont share attributes.
     */
    @Test
    public void testObservableSubscriptionSync() {
        Observable.from(numbers)
                  .doOnNext(increaseTotalItemsEmitted())
                  .subscribe(number -> System.out.println("Items emitted:"+total));

        System.out.println("I finish after the observable finish.  Items emitted:"+total);
    }


    private Action1<Integer> increaseTotalItemsEmitted() {
        return number->{
            try {
                total+=number;
                System.out.println("current total value:"+total);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }


}
