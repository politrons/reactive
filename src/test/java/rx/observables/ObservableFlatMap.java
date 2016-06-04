package rx.observables;

import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Sometimes we want to pass an observable through the pipeline, that´s when flatMap come handy.
 * Also it´ very useful combine flapMap with onSubscribe if we want make our pipeline async.
 *
 */
public class ObservableFlatMap {

    static int count = 0;

    /**
     * We emmit all items from the Observable source after apply a function, in this case create a new observable of Person.
     * Emitted:Person{name='Pablo', age=34, sex='male'}
     * Person{name='Paula', age=35, sex='female'}
     */
    @Test
    public void testFlatMap() {

        List<Person> people = new ArrayList<>();
        people.add(new Person("Pablo", 34, "male"));
        people.add(new Person("Paula", 35, "female"));
        Observable.just(people)
                  .flatMap(Observable::from)
                  .subscribe(this::showResult, System.out::println, () -> System.out.println("complete"));
    }

    private void showResult(Person person) {
        System.out.println(person.toString());
        System.out.println(++count);
    }

    private String result = "";

    /**
     * Internally in the flatMap, every element in the collection is passed through the pipeline as a new observable.
     */
    @Test
    public void testFlatMapContact() {
        long start = System.currentTimeMillis();
        List<String> words = new ArrayList<>();
        words.add("Hello ");
        words.add("Reactive ");
        words.add("World");
        Observable.just(words)
                  .flatMap(Observable::from)
                  .subscribe(this::contactWords, System.out::println,
                             () -> System.out.println("Result " + result + " in " + (System.currentTimeMillis() - start)));

    }

    /**
     * Since we are creating a new observable per word in the array thanks of flapMap, we set that every new observable will be executed in a new thread
     * Making this pipeline asynchronous
     */
    @Test
    public void testFlatMapAsyncContact() {
        long start = System.currentTimeMillis();
        List<String> words = new ArrayList<>();
        words.add("Hello ");
        words.add("Reactive ");
        words.add("World");
        Observable.from(words)
                  .flatMap(word -> Observable.just(word)
                                                .subscribeOn(Schedulers.newThread()))
                  .subscribe(this::contactWords, System.out::println,
                             () -> System.out.println("Result " + result + " in " + (System.currentTimeMillis() - start)));

    }


    private void contactWords(String word) {
        System.out.println("Thread:"+Thread.currentThread().getName());
        result = result.concat(word);
    }

}
