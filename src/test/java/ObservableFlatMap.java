import org.junit.Test;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;


public class ObservableFlatMap<T> {

    static int count = 0;

    /**
     * We emmit all items from the Observable source after apply a function, in this case create a new observable of Person
     */
    @Test
    public void testFlatMap() {

        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Pablo", 34, "male"));
        persons.add(new Person("Paula", 35, "female"));
        Observable.just(persons)
                  .flatMap(Observable::from)
                  .subscribe(this::showResult);


    }

    private void showResult(Person person) {
        System.out.println(person.toString());
        System.out.println(++count);
    }

}
