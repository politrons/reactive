package rx.observables.creating;

import org.junit.Test;
import rx.Observable;


/**
 * @author Pablo Perez
 */

/**
 * Normally when you create an observable with just or create, The observable is created with the value that passed at that point,
 * and then once that a observer subscribe, the value it´s just passed through the pipeline.
 * Sometimes that´s not the desirable, since maybe we dont want to  create the observable at that point, only when an observer subscribe to it.
 * Defer it will wait to create the observable with the value when we subscribe our observer.
 * Basically create this Observable that wrap the observable that we want to create only when we subscribe to the observable.
 */
public class ObservableDefer {


    String value = "none";

    public void setValue(String value) {
        this.value = value;
    }

    public Observable<String> getValue() {
        return Observable.just(value);
    }

    public Observable<String> getDeferValue() {
        return Observable.defer(() -> Observable.just(value));
    }

    /**
     * In this example we see how the values are set into the observable once we create instead when we subscribe.
     * Shall print
     *       none
     */
    @Test
    public void testNotDeferObservable() {
        Observable<String> observable = getValue();
        setValue("deferred");
        observable.subscribe(System.out::println);
    }

    /**
     * In this example we see how the values are set into the observable once we subscribe instead when we create the observable.
     * Shall print
     *       deferred
     */
    @Test
    public void testDeferObservable() {
        Observable<String> observable = getDeferValue();
        setValue("deferred");
        observable.subscribe(System.out::println);
    }

}