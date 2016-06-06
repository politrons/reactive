package rx.observables.combining;

import org.junit.Test;
import rx.Observable;
import rx.observables.transforming.Person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * Merge get all observables defined in it, and set into an array,
 * then iterate over the array emitting all observables through the pipeline.
 */
public class ObservableMerge {

    static int count = 0;

    /**
     * Since we merge the two observables, once that we subscribe we will emit both.
     */
    @Test
    public void testMerge() {
        Observable.merge(obPerson(), obPerson1())
                  .subscribe(result -> showResult(result.toString()));
    }

    /**
     * Here we merge two list and we sort both
     */
    @Test
    public void testMergeChains() {
        Observable.merge(Observable.from(Arrays.asList(1, 2, 13, 11, 5)), Observable.from(Arrays.asList(10, 4, 12, 3, 14, 15)))
                  .collect(ArrayList<Integer>::new, ArrayList::add)
                  .doOnNext(Collections::sort)
                  .subscribe(System.out::println);

    }


    private void showResult(String s) {
        System.out.println(s);
    }

    public Observable<Person> obPerson() {
        return Observable.just(new Person("pablo", 34, null));
    }

    public Observable<Person> obPerson1() {
        return Observable.just(new Person(null, 25, "male"));
    }

}
