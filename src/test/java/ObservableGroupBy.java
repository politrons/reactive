import org.junit.Test;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;


public class ObservableGroupBy {

    @Test
    public void testGroupBy() {
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Pablo", 34, "male"));
        persons.add(new Person("Paula", 35, "female"));
        Observable.just(persons)
                  .flatMap(listOfPersons -> Observable.from(listOfPersons)
                                                      .groupBy(person -> person.sex.equals("male"))).subscribe(booleanPersonGroupedObservable -> {
            if(booleanPersonGroupedObservable.getKey()){
                booleanPersonGroupedObservable.asObservable().subscribe(person -> System.out.println("Here the male:" + person.name));
            }else{
                booleanPersonGroupedObservable.asObservable().subscribe(person -> System.out.println("Here the female:" + person.name));
            }
        });
    }


}
