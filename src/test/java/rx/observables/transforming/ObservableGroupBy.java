package rx.observables.transforming;

import org.junit.Test;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * Group by can be very handy when you need create items groups in your pipeline, instead of filter by just one item type,
 * you can create those groups and then when the observable finish, on complete you will have a GroupedObservable, which is a map
 * with the pair key(filter)/value(item emitted)
 */
public class ObservableGroupBy {

    /**
     * In this example we create a boolean/Person group.
     * The key of the group is just the boolean value of the condition if the item emitted is male
     */
    @Test
    public void testGroupBy() {
        Observable.just(getPersons())
                  .flatMap(listOfPersons -> Observable.from(listOfPersons)
                                                      .groupBy(person -> person.sex.equals("male")))
                  .subscribe(booleanPersonGroupedObservable -> {
                      if (booleanPersonGroupedObservable.getKey()) {
                          booleanPersonGroupedObservable.asObservable()
                                                        .subscribe(person -> System.out.println("Here the male:" + person.name));
                      } else {
                          booleanPersonGroupedObservable.asObservable()
                                                        .subscribe(person -> System.out.println("Here the female:" + person.name));
                      }
                  });
    }


    /**
     * In this example we create a String/Person group.
     * The key of the group is just the String value of the sex of the item.
     */
    @Test
    public void testGroupBySex() {
        Observable.just(getPersons())
                  .flatMap(listOfPersons -> Observable.from(listOfPersons)
                                                      .groupBy(person -> person.sex))
                  .subscribe(booleanPersonGroupedObservable -> {
                      switch (booleanPersonGroupedObservable.getKey()) {
                          case "male": {
                              booleanPersonGroupedObservable.asObservable()
                                                            .subscribe(person -> System.out.println("Here the male:" + person.name));
                              break;
                          }
                          case "female": {
                              booleanPersonGroupedObservable.asObservable()
                                                            .subscribe(person -> System.out.println("Here the female:" + person.name));
                              break;
                          }
                      }
                  });
    }

    private List<Person> getPersons() {
        List<Person> people = new ArrayList<>();
        people.add(new Person("Pablo", 34, "male"));
        people.add(new Person("Paula", 35, "female"));
        return people;
    }

}
