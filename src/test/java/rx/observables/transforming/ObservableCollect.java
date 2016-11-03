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
 *
 */
public class ObservableCollect {

    @Test
    public void collectObservableList() {
        Observable.from(Arrays.asList(1, 2))
                .flatMap(item -> getFirstList())
                .collect(ArrayList<Integer>::new, (lastList, newList) ->
                        Stream.concat(lastList.stream(),
                                newList.stream()).collect(Collectors.toList()))
                .subscribe(System.out::println);

    }

    private Observable<List<Integer>> getFirstList() {
        return Observable.just(Arrays.asList(1, 2, 3, 4));
    }


}
