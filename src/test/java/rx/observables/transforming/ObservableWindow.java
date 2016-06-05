package rx.observables.transforming;

import org.junit.Test;
import rx.Observable;


/**
 * @author Pablo Perez
 *
 *         Window is similar to buffer, but instead emitt the list of items buffered, it will return a new observable with those items.
 */
public class ObservableWindow {


    /**
     * In this example since we set the window in 3 items, it will create two observables.
     * First one will emit 0,1,2 item, and second will emit 3,4
     */
    @Test
    public void windowCountObservable() {
        Integer[] numbers = {0, 1, 2, 3, 4};

        Observable.from(numbers)
                  .window(3)
                  .flatMap(o-> {
                      System.out.println("New Observable");
                      return o;
                  })
                  .subscribe(number -> System.out.println("Number:" + number));

    }

}
