package rx.observables.utils;

import org.junit.Test;
import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;


public class ObservableRecursive {

    boolean found = false;

    @Test
    public void runTest() {
        recursive(Observable.from(getNumbers()))
                .collect(ArrayList<Integer>::new, ArrayList::add)
                .subscribe(System.out::println);
    }

    public Observable<Integer> recursive(Observable<Integer> numbers) {
        return numbers.flatMap(number -> {
            if (number == 4 && !found) {
                found = true;
                return Observable.just(number).concatWith(recursive(Observable.from(getNumbers())));
            }
            return Observable.just(number);
        });

    }

    private ArrayList<Integer> getNumbers() {
        return new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
    }





}
