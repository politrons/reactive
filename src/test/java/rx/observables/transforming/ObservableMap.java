package rx.observables.transforming;

import org.junit.Test;
import rx.Observable;

/**
 * Using map we can transform the item passed to the observable into a new item just in our pipeline.
 * Remember that this is functional so nothing is mutable here, everything inside the pipeline must be final.
 */
public class ObservableMap {

    /**
     * We create an observable with String apple, and we change that value to another inside our pipeline.
     * Through the pipeline we create new fruits and finally the value passed to the observer is banana.
     * But the old variable remains unaltered
     * Emitted: banana
     */
    @Test
    public void testMap() {
        String fruit = "apple";
        Observable.just(fruit)
                  .map(apple -> "orange")
                  .map(orange -> "banana")
                  .subscribe(banana -> System.out.println("I´m a " + banana));
        System.out.println("I´ still a " + fruit);
    }


}
