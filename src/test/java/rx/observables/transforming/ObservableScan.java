package rx.observables.transforming;

import org.junit.Test;
import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Pablo Perez
 *         Scan it just works as redude on Java 8 Stream, pass into the function the last emitted item and the new one.
 */
public class ObservableScan {


    /**
     * apply this function for every item against the previous emitted item from the source.
     * Emitted:
     * 0
     * 1
     * 3
     * 6
     * 10
     * 15
     */
    @Test
    public void scanObservable() {
        Integer[] numbers = {0, 1, 2, 3, 4, 5};
        Observable.from(numbers)
                .scan((lastItemEmitted, newItem) -> lastItemEmitted + newItem)
                .subscribe(System.out::println);
    }

    @Test
    public void scanObservableIntoList() {
        Integer[] numbers = {0, 1, 2, 3, 4, 5};
        Observable.from(numbers)
                .scan(new ArrayList<>(),
                        (lastItemEmitted, newItem) -> {
                            lastItemEmitted.add(newItem);
                            return lastItemEmitted;
                        })
                .subscribe(System.out::println);
    }

    @Test
    public void scanObservableList() {
        Observable.from(Arrays.asList(1, 2))
                .flatMap(item -> getFirstList())
                .scan((lastList, newList) ->
                        Stream.concat(lastList.stream(),
                                newList.stream()).collect(Collectors.toList()))
                .subscribe(System.out::println);

    }

    private Observable<List<Integer>> getFirstList() {
        return Observable.just(Arrays.asList(1, 2, 3, 4));
    }


}
