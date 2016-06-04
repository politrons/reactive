package rx;

import org.junit.Test;


/**
 * @author Pablo Perez
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