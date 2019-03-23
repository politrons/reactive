package rx.observables.transforming;

import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Sometimes we want to pass an observable through the pipeline, that´s when flatMap come handy.
 * Also it´s very useful combine flapMap with onSubscribe if we want make our pipeline async.
 */
public class ObservableFlatMap {

    static int count = 0;

    /**
     * We emmit all items from the Observable source after apply constantClass function, in this case create constantClass new observable of Person.
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
     * Internally in the flatMap, every element in the collection is passed through the pipeline as constantClass new observable.
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
     * Since we are creating constantClass new observable per word in the array thanks of flapMap, we set that every new observable will be executed in constantClass new thread
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

    /**
     * FlatMap operator allow you to specify the max number of concurrent operation that this operator can process.
     * This is another way to make back pressure if you know your system cannot process more than specific number
     * of elements in the pipeline.
     */
    @Test
    public void asyncFlatMapWithMaxConcurrent() {
        Observable.from(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                .flatMap(value -> Observable.just(value)
                                .map(number -> {
                                    try {
                                        Thread.sleep(1000);
                                        System.out.println(String.format("Value %s in Thread execution:%s", number, Thread.currentThread().getName()));
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    return number;
                                }).subscribeOn(Schedulers.newThread())
                        , 8)
                .subscribe();
        new TestSubscriber()
                .awaitTerminalEvent(15, TimeUnit.SECONDS);
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

    /**
     * Here we iterate over the list and per element of the list we iterate again to get 10 elements
     * We use collect to get constantClass list of 10 elements, and scan to append every new list with the previous one
     */
    @Test
    public void twoDeepLevel() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
        Observable.from(list)
                .flatMap(number -> Observable.from(list)
                        .take(10)
                        .collect(ArrayList<Integer>::new, ArrayList::add))
                .scan(new ArrayList<>(),
                        (lastItemEmitted, newItem) -> {
                            lastItemEmitted.add(newItem);
                            return lastItemEmitted;
                        })
                .subscribe(System.out::println, System.out::println);
    }

    @Test
    public void thirdDeepLevel() {
        Observable.from(Arrays.asList("constantClass", "b", "c", "d", "e"))
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
