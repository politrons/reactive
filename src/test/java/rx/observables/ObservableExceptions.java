package rx.observables;

import org.junit.Test;
import rx.Observable;


/**
 * @author Pablo Perez
 */

/**
 *
 * Observable pipeline does not allow Exception, just runtimeExceptions,
 * that´s why if your code run into pipeline can throw an exception your observable wont compile.
 * In order to fix it you will have to pass through the pipeline runtime exceptions
 */
public class ObservableExceptions {


    /**
     * Here is a silly example how in order to make your pipeline compile you must catch the exception and parse it as Runtime exception
     */
    @Test
    public void observableException() {
        Integer[] numbers = {0, 1, 2, 3, 4, 5};

        Observable.from(numbers)
                  .doOnNext(number -> {
                          if(number > 3){
                              try {
                                  throw new IllegalArgumentException();
                              } catch (Exception e) {
                                  throw new RuntimeException(e);
                              }
                          }

                  })
                  .doOnError(t-> System.out.println("Expecting illegal argument exception:"+t.getMessage()))
                  .subscribe();

    }

}
