package rx.observables;

import org.junit.Test;
import rx.Observable;


public class ObservableConcat {

    /**
     * Get every emitted item and concat with the previous emitted item
     */
    @Test
    public void testContact() {

        Observable.concat(Observable.just("Hello"),
                          Observable.just("reactive"),
                          Observable.just("world"))
                  .subscribe(System.out::println);
    }


}
