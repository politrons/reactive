package rx.observables;

import org.junit.Test;
import rx.Observable;


/**
 * @author Pablo Perez
 */

/**
 * Normally when you create an observable with just or create, the value is just passed to the observable,
 * and then once that a observer subscribe, it´s just passed through the pipeline.
 * Sometimes that´s not the desirable, since maybe we want to just create the observable but not subscribe to it yet.
 * Defer it will wait to take that value and set into the observable when we subscribe our observer.
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
     * will return none
     */
    @Test
    public void testNotDeferObservable() {
        Observable<String> observable = getValue();
        setValue("deferred");
        observable.subscribe(System.out::println);
    }

    /**
     * In this example we see how the values are set into the observable once we subscribe instead when we create the observable.
     * Will return deferred
     */
    @Test
    public void testDeferObservable() {
        Observable<String> observable = getDeferValue();
        setValue("deferred");
        observable.subscribe(System.out::println);
    }


}