package rx;

import org.junit.Test;
import rx.Single;
import rx.schedulers.Schedulers;

import static rx.Single.just;
import static rx.Single.zip;


/**
 * @author Pablo Perez
 */
public class SingleFeatures {


    @Test
    public void testSingle() {
        just("Single").subscribe(result -> System.out.println("Result: " + result), (error) -> System.out.println("Something went wrong" + error.getMessage()));
    }


    @Test
    public void testZipSingles() {
        Single<Integer> single = just(1);
        Single<Integer> single2 = just(2);
        zip(single, single2, (s1, s2) -> s1 + s2).subscribe(result -> System.out.println("Result: " + result),
                                                            (error) -> System.out.println("Something went wrong" + error.getMessage()));
    }

    @Test
    public void testSinglesAsync() {
        System.out.println("Current thread:" + Thread.currentThread()
                                                     .getName());
        just("Single").observeOn(Schedulers.newThread())
                      .subscribe(result -> System.out.println("Async Result in thread: " + Thread.currentThread()
                                                                                                 .getName()),
                                 (error) -> System.out.println("Something went wrong" + error.getMessage()));
    }

}
