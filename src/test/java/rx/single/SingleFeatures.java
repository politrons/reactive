package rx.single;

import org.junit.Test;
import rx.Single;
import rx.schedulers.Schedulers;


/**
 * @author Pablo Perez
 * Single is just another variant of observable as relay. In this particular case Single only emit one item.
 * Since only emit 1 item in the pipeline, single  only accept two Action functions, onSuccess and onError.
 */
public class SingleFeatures {


    /**
     * Here we can see a basic example where Single just emit the item and onSuccess and onError functions are defined.
     */
    @Test
    public void testSingle() {
        Single.just("Single").subscribe(result -> System.out.println("Result: " + result),
                                        (error) -> System.out.println("Something went wrong" + error.getMessage()));
    }


    /**
     * The fact that Single only emit 1 item does not means that cannot use all the ReactiveX features as zip, merge, concat, and so on.
     * Here we use Zip to run two singles, which every one of those it will emit just 1 item.
     */
    @Test
    public void testZipSingles() {
        Single<Integer> single = Single.just(1);
        Single<Integer> single2 = Single.just(2);
        Single.zip(single, single2, (s1, s2) -> s1 + s2)
              .subscribe(result -> System.out.println("Result: " + result),
                                                            (error) -> System.out.println("Something went wrong" + error.getMessage()));
    }

    /**
     * Also it´ possible use Single asynchronously using subscribeOn or observerOn
     */
    @Test
    public void testSinglesAsync() {
        System.out.println("Current thread:" + Thread.currentThread()
                                                     .getName());
        Single.just("Single").subscribeOn(Schedulers.newThread())
                      .subscribe(result -> System.out.println("Async Result in thread: " + Thread.currentThread()
                                                                                                 .getName()),
                                 (error) -> System.out.println("Something went wrong" + error.getMessage()));
    }

}
