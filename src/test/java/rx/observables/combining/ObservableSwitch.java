package rx.observables.combining;

import org.junit.Test;
import rx.Observable;
import rx.observables.transforming.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Switch allow switch the item emitted by the observable by another value.
 */
public class ObservableSwitch {

    /**
     * We switchIfEmpty switch from alternative observable if the origin observable is empty.
     * Emitted:Person{name='new', age=0, sex='no_sex'}
     */
    @Test
    public void testSwitchPerson() {
        Observable.just(new ArrayList<>())
                  .flatMap(persons -> Observable.from(persons)
                                                .switchIfEmpty(Observable.just(new Person("new", 0, "no_sex"))))
                  .subscribe(System.out::println);

    }

    /**
     * In this case SwitchIfEmpty does not switch from alternative observable because the origin observable is not empty.
     * Emitted:Person{name='Pablo', age=34, sex='male'}
     */
    @Test
    public void testNoSwitchPerson() {
        List<Person> people = new ArrayList<>();
        people.add(new Person("Pablo", 34, "male"));
        Observable.just(people)
                  .flatMap(persons -> Observable.from(persons)
                                                .switchIfEmpty(Observable.just(new Person("new", 0, "no_sex"))))
                  .subscribe(System.out::println);

    }

    /**
     * We switch from original item to a new observable just using switchMap.
     * It´s a way to replace the Observable instead just the item as map does
     * Emitted:Person{name='Pablo', age=34, sex='male'}
     */
    @Test
    public void testSwitchMap() {
        Observable.just(new Person("Pablo", 34, "male"))
                  .switchMap(person -> Observable.just(new Person("Pablo", 0, "no_sex")))
                  .subscribe(System.out::println);

    }

}
