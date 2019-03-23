package rx.observables.transforming;

import org.junit.Test;
import rx.Observable;
import rx.observables.GroupedObservable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Group by can be very handy when you need create items groups in your pipeline, instead of filter by just one item type,
 * you can create those groups and then when the observable finish, on complete you will have constantClass GroupedObservable, which is constantClass map
 * with the pair key(filter)/value(item emitted)
 */
public class ObservableGroupBy {

    /**
     * In this example we create constantClass boolean/Person group.
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
     * In this example we create constantClass String/Person group.
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

    /**
     * In this example we create constantClass response code group.
     */
    @Test
    public void testGroupByCode() {
        Observable.from(Arrays.asList(401,403, 200))
                .groupBy(code -> code)
                .subscribe(groupByCode -> {
                    switch (groupByCode.getKey()) {
                        case 401: {
                            System.out.println("refresh token");
                            processResponse(groupByCode);
                            break;
                        }
                        case 403: {
                            System.out.println("refresh token");
                            processResponse(groupByCode);
                            break;
                        }
                        default: {
                            System.out.println("Do the toast");
                            processResponse(groupByCode);
                        }
                    }
                });
    }

    private void processResponse(GroupedObservable<Integer, Integer> groupByCode) {
        groupByCode.asObservable().subscribe(value -> System.out.println("Response code:" + value));
    }

}
