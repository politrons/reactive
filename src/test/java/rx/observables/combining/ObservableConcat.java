package rx.observables.combining;

import org.junit.Test;
import rx.Observable;


/**
 * Con
 */
public class ObservableConcat {

    /**
     * Get every emitted item and concat with the previous emitted item
     * Shall print
     * Hello
     * reactive
     * world
     */
    @Test
    public void testContact() {

        Observable.concat(Observable.just("Hello"),
                          Observable.just("reactive"),
                          Observable.just("world"))
                .subscribe(System.out::println);
    }


}
