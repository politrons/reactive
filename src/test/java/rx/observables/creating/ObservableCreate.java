package rx.observables.creating;

import org.junit.Test;
import rx.Observable;


/**
 * @author Pablo Perez
 */
public class ObservableCreate {


    /**
     * With the create operator, we are able to specify the items emitted on next or on error,
     * to the observer
     */
    @Test
    public void testCreateObservableNext() {
        Observable.create(observer -> {
            observer.onNext("Injected value on Next");
        }).map(s -> ((String) s).toUpperCase())
                .subscribe(System.out::println, System.out::println);
    }

    /**
     * With the create operator, we are able to specify the items emitted on next or on error,
     * to the observer
     */
    @Test
    public void testCreateObservableError() {
        Observable.create(observer -> {
            observer.onError(new NullPointerException("This is the final exception"));
        })
                .map(s -> ((String) s).toUpperCase())
                .subscribe(System.out::println, System.out::println);
    }


    @Test
    public void returnObservableInCreate() {
        Integer[] numbers = {0, 1, 2, 3, 4};
        Observable.create(observer -> observer.onNext(totalReadNumbers(numbers)))
                .subscribe(n -> System.out.println(n + " in thread " + Thread.currentThread().getName()),
                           System.out::println);
    }

    private Integer totalReadNumbers(Integer[] numbers) {
        return Observable.from(numbers)
                .buffer(3)
                .map(l -> l.stream()
                        .reduce((x, y) -> x + y))
                .doOnNext(n -> System.out.println("Buffer Thread:" + Thread.currentThread().getName()))
                .toBlocking().first().get();
    }


}