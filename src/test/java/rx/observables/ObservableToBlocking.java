package rx.observables;

import org.junit.Test;
import rx.Observable;

import static junit.framework.TestCase.assertTrue;


/**
 * @author Pablo Perez
 * Using toBlocking we just transform an observable into BlockingObservable,
 * which is handy on mock or test modules to extract the value of the observable using single method
 * As the documentations says, is not a good practice use it, and it would not be a good practice on production code.
 */
public class ObservableToBlocking {

    /**
     * Create an observable with a int value we evolve to String and return String value without subscribe
     */
    @Test
    public void observableEvolveAndReturnToStringValue(){
        assertTrue(Observable.just(10)
                  .map(String::valueOf)
                  .toBlocking().single().equals("10"));
    }


    /**
     * Evolve observable to boolean and return plain value
     */
    @Test
    public void observableGetBooleanLogicOperationAndReturnBooleanValue(){
        assertTrue(Observable.just(10)
                             .map(intValue -> intValue == 10)
                             .toBlocking()
                             .single());
    }

    /**
     * Evolve observable to boolean and return plain value
     */
    @Test
    public void observableGetIntValueAndReturnListInt(){
        Observable.just(10).toList().forEach(System.out::println);
    }

}
