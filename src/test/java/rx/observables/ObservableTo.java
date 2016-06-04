package rx.observables;

import org.junit.Test;
import rx.Observable;

import static junit.framework.TestCase.assertTrue;


/**
 * @author Pablo Perez
 */
public class ObservableTo {

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
