package rx.observables.transforming;

import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Sometimes we want to pass an observable through the pipeline, that´s when flatMap come handy.
 * Also it´s very useful combine flapMap with onSubscribe if we want make our pipeline async.
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

    /**
     * In flatMap all Observable created in the flatMap are executed in the main thread in sequential order.
     */
    @Test
    public void flatMapThreads() {
        Observable.from(Arrays.asList(1, 2, 3, 4))
                .flatMap(number -> Observable.just(number)
                        .doOnNext(n -> System.out.println(String.format("Executed in thread:%s number %s",
                                Thread.currentThread().getName(), n))))
                .subscribe();
    }

    /**
     * Shall print 3
     */
    @Test
    public void multiFlatMap() {

        Observable.just(1)
                .flatMap(x -> Observable.just(2), (x, y) -> x + y)
                .subscribe(System.out::println);
    }

    private void contactWords(String word) {
        System.out.println("Thread:" + Thread.currentThread().getName());
        result = result.concat(word);
    }

    @Test
    public void flatMapCities() {
        List<String> cities = Arrays.asList("London", "Berlin", "Moscow");
        Observable.from(cities)
                .flatMap(city -> getReport(city)
                        .doOnNext(report -> checkReport(city, report)));
    }

    private void checkReport(String city, String report) {
        //TODO:Check here the report and city
    }

    private Observable<String> getReport(String city) {
        return Observable.just("report");
    }

    @Test
    public void flatMapCars() {
        Observable.from(getCars())
                .flatMap(this::saveCar)
                .subscribe();
    }

    private List<String> getCars() {
        return Arrays.asList("Aston martin", "Renault", "Seat");
    }

    private Observable<String> saveCar(String car) {
        return Observable.just(car);//You should save the car
    }


    @Test
    public void thirdDeepLevel() {
        Observable.from(Arrays.asList("a", "b", "c", "d", "e"))
                .flatMap(letter -> Observable.from(Arrays.asList(1, 2, 3, 4, 5))
                        .map(number -> {
                            System.out.println(letter + ":" + number);
                            return number;
                        })
                        .filter(number -> number == 4)
                        .flatMap(number -> Observable.from(Arrays.asList("f", "g", "h", "i"))
                                .map(leter2 -> {
                                    System.out.println(letter + ":" + number);
                                    return leter2;
                                })))
                .subscribe();
    }

}
