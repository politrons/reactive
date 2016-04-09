import org.junit.Test;
import rx.Observable;


public class ObservableConcat<T> {

    /**
     * Get every emitted item and concat with the previous emmitted item
     */
    @Test
    public void testContact() {

        Observable.concat(Observable.just("Hello"),
                          Observable.just("reactive"),
                          Observable.just("world"))
                  .subscribe(System.out::println);
    }


}
