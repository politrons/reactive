package rx.observables.combining;

import org.junit.Test;
import rx.Observable;

import java.util.concurrent.TimeUnit;


/**
 * @author Pablo Perez
 */
public class ObservableChain {


    @Test
    public void testObservableChain() {

        Observable.just(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, new Integer[]{10, 11, 12, 13, 14, 15, 16, 17, 18})
                  .flatMap(num -> Observable.from(num)
                                            .flatMap(i1 -> Observable.just(i1)
                                                                     .doOnNext(item -> System.out.println("sending:" + item))
                                                                     .delay(50, TimeUnit.MILLISECONDS), 1)
                                            .flatMap(i2 -> Observable.just(i2)
                                                                     .delay(50, TimeUnit.MILLISECONDS)
                                                                     .doOnNext(i3 -> System.out.println("completed:" + i3)), 1))
                  .toBlocking()
                  .last();
    }
}
