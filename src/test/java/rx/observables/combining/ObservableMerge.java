package rx.observables.combining;

import org.junit.Test;
import rx.Observable;
import rx.Scheduler;
import rx.observables.transforming.Person;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * Merge get all observables defined in it, and set into an array,
 * then iterate over the array emitting all observables through the pipeline.
 */
public class ObservableMerge {

    /**
     * Since we merge the two observables, once that we subscribe we will emit both.
     * Shall print
     * Person{name='pablo', age=34, sex='null'}
     * Person{name='null', age=25, sex='male'}
     */
    @Test
    public void testMerge() {
        Observable.merge(obPerson(), obPerson1())
                .subscribe(result -> showResult(result.toString()));
    }

    /**
     * Here we merge two list and we sort the list for every new item added into.
     * Shall return
     * <p>
     * [1, 2, 3, 4, 5, 10, 11, 12, 13, 14, 15]
     */
    @Test
    public void testMergeLists() {
        Observable.merge(Observable.from(Arrays.asList(2, 1, 13, 11, 5)), Observable.from(Arrays.asList(10, 4, 12, 3, 14, 15)))
                .collect(ArrayList<Integer>::new, ArrayList::add)
                .doOnNext(Collections::sort)
                .subscribe(System.out::println);

    }

    /**
     * Using mergeDelayError we ensure that all items merged are emitted, and if one of them throw an error it
     * wont finish onComplete callback once all items are emitted but onError
     */
    @Test
    public void testMergeDelayError() {
        Scheduler scheduler = Schedulers.newThread();
        Observable.mergeDelayError(
                Observable.error(new RuntimeException())
                        .observeOn(scheduler)
                        .subscribeOn(Schedulers.io()),
                Observable.just("Hello")
                        .observeOn(scheduler)
                        .subscribeOn(Schedulers.io()))
                .finallyDo(() -> System.out.println("Finally action"))
                .subscribe(System.out::println,
                        System.out::println,
                        () -> System.out.println("On complete it should never happen"));
    }


    private void showResult(String s) {
        System.out.println(s);
    }

    private Observable<Person> obPerson() {
        return Observable.just(new Person("pablo", 34, null));
    }

    private Observable<Person> obPerson1() {
        return Observable.just(new Person(null, 25, "male"));
    }


    @Test
    public void testMergeMaxConcurrency() {
        Observable.merge(Observable.just(
                Observable.just(3),
                Observable.just(5),
                Observable.just(1),
                Observable.just(4),
                Observable.just(2)), 2)
                .collect(ArrayList<Integer>::new, ArrayList::add)
                .doOnNext(Collections::sort)
                .subscribe(System.out::println);

    }


}
