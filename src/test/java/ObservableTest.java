import org.junit.Test;
import rx.Observable;

import java.util.stream.IntStream;


/**
 * @author Pablo Perez
 */
public class ObservableTest {


    @Test
    public void testObservable(){
        Integer[] numbers = { 0, 1, 2, 3, 4, 5 };

        Observable numberObservable = Observable.from(numbers);

        numberObservable.subscribe(
                (incomingNumber) -> System.out.println("incomingNumber " + incomingNumber),
                (error) -> System.out.println("Something went wrong" + ((Throwable)error).getMessage()),
                () -> System.out.println("This observable is finished")
        );


    }

}
