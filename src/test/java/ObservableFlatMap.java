import org.junit.Test;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;


public class ObservableFlatMap<T> {

    static int count = 0;

    /**
     * We emmit all items from the Observable source after apply a function, in this case create a new observable of Person.
     * Emitted:Person{name='Pablo', age=34, sex='male'}
               Person{name='Paula', age=35, sex='female'}
     */
    @Test
    public void testFlatMap() {

        List<Person> people = new ArrayList<>();
        people.add(new Person("Pablo", 34, "male"));
        people.add(new Person("Paula", 35, "female"));
        Observable.just(people)
                  .flatMap(Observable::from)
                  .subscribe(this::showResult);
    }

    private void showResult(Person person) {
        System.out.println(person.toString());
        System.out.println(++count);
    }

}
