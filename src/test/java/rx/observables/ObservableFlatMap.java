package rx.observables;

import org.junit.Test;
import rx.Observable;
import rx.Person;

import java.util.ArrayList;
import java.util.List;


public class ObservableFlatMap<T> {

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
                  .subscribe(this::showResult, System.out::println, ()-> System.out.println("complete"));
    }

    private void showResult(Person person) {
        System.out.println(person.toString());
        System.out.println(++count);
    }

    private String result="";

    @Test
    public void testFlatMapContact() {
        List<String> words = new ArrayList<>();
        words.add("Hello ");
        words.add("Reactive ");
        words.add("World");
        Observable.just(words)
                  .flatMap(Observable::from)
              .subscribe(this::contactWords, System.out::println, ()-> System.out.println(result));

    }

    private void contactWords(String word) {
        result = result.concat(word);
    }

}
