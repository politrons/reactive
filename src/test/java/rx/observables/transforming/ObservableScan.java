package rx.observables.transforming;

import org.junit.Test;
import rx.Observable;


/**
 * @author Pablo Perez
 * Scan it just works as redude on Java 8 Stream, pass into the function the last emitted item and the new one.
 */
public class ObservableScan {


    /**
     * apply this function for every item against the previous emitted item from the source.
     *  Emitted:
                0
                1
                3
                6
                10
                15
     */
    @Test
    public void scanObservable() {
        Integer[] numbers = {0, 1, 2, 3, 4, 5};
        Observable.from(numbers)
                  .scan((lastItemEmitted, newItem) -> (lastItemEmitted + newItem))
                  .subscribe(System.out::println);
    }

}
