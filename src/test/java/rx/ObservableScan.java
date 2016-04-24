package rx;

import org.junit.Test;
import rx.Observable;


/**
 * @author Pablo Perez
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
