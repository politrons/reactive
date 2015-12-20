import org.junit.Test;
import rx.Observable;

import java.util.concurrent.TimeUnit;


/**
 * @author Pablo Perez
 */
public class CreatingObservable {


    @Test
    public void testObservable(){
        Integer[] numbers = { 0, 1, 2, 3, 4, 5 };

        Observable.from(numbers).subscribe(
                (incomingNumber) -> System.out.println("incomingNumber " + incomingNumber),
                (error) -> System.out.println("Something went wrong" + error.getMessage()),
                () -> System.out.println("This observable is finished")
        );
    }

    /**
     * Create an observable with a Long value evey second
     */
    @Test
    public void intervalObservable(){
        Observable.interval(1, TimeUnit.SECONDS)
                  .map(Long::new)
                  .subscribe(result-> System.out.printf(String.valueOf(result)));
    }


}
