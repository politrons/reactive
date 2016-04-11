import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;


/**
 * @author Pablo Perez
 */
public class ObservableScheduler {

    private String foo="empty";

    int total = 0;

    private Scheduler scheduler = Schedulers.newThread();

    /**
     * In this test we prove how when we subscribe a observable using scheduler, this one is executed in another thread, and they dont share attributes.
     */
    @Test
    public void testObservableSubscriptionAsync() {
        Integer[] numbers = {0, 1, 2, 3, 4};

        Observable.from(numbers)
                  .flatMap(Observable::just)
                  .doOnNext(number->{
                      try {
                          total+=number;
                          System.out.println("current total value:"+total);
                          Thread.sleep(100);
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }
                  })
                  .subscribeOn(scheduler)
                  .subscribe(number -> System.out.println("Item emitted:"+total));
        System.out.println("I finish before the observable finish to items all items:"+total);
    }


}
