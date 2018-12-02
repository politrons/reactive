package reactor;

import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

/**
 * Flux is like the ReactiveX observable, The Flux implement Publisher, and once we subscribe to
 * the Flux we receive a Disposable.
 * <p>
 * Like the rest of Reactive Stream implementations we have three callbacks in the disposable.
 * <p>
 * OnNext -> To be invoked per item emitted in the pipeline.
 * OnError -> To be invoked per error emitted in the pipeline. Once that an error happens the emission is stopped.
 * OnComplete -> To be invoked once we finish the emission of items in the pipeline.
 * <p>
 * The Disposable has a boolean isDisposable which return true/false depending if the pipeline is finish so the subscriber is unsubscribed.
 */
public class ReactorCreating {

    /**
     * A simple way to create a Flux. Allow you to add one or N elements.
     * Then once that you subscribe your Flux and create a Disposable the emission of the items start.
     **/
    @Test
    public void just() {
        Disposable subscribe = Flux.just("hello", "reactive", "Spring", "world", "?")
                .filter(value -> value.length() > 1)
                .map(String::toUpperCase)
                .subscribe(value -> System.out.println("On next callback: " + value),
                        t -> System.out.println("On error callback: " + t),
                        () -> System.out.println("On complete callback"));

        System.out.println("It disposable:" + subscribe.isDisposed());
    }

    @Test
    public void justError() {
        Flux.just("hello", "reactive", "Spring", null, "?")
                .filter(value -> value.length() > 1)
                .subscribe(value -> System.out.println("On next callback: " + value),
                        t -> System.out.println("On error callback: " + t),
                        () -> System.out.println("On complete callback"));
    }

    /**
     * From operator allow you to create a Flux from another flux combining pipelines logic.
     * In this case we have the first pipeline which filter the number of elements, and the second one
     * set in upper case all elements emitted.
     */
    @Test
    public void from() {
        Flux<String> flux = Flux.just("hello", "reactive", "Spring", "world", "?")
                .filter(value -> value.length() > 1);
        Flux.from(flux)
                .map(String::toUpperCase)
                .subscribe(System.out::println);
    }

    /**
     * Since interval work asynchronously you will have to use Thread sleep to wait a period of time
     * to see some items emitted. This type of Flux never finish to emit, in order to stop, you will need unsubscribe the Disposable.
     * <p>
     * In interval since it´ never finish to emmit items in the pipeline, you need explicitly unsubscribe from the pipeline using
     * **subscribe.dispose()**
     */
    @Test
    public void interval() throws InterruptedException {
        Disposable subscribe = Flux.interval(Duration.of(1, ChronoUnit.SECONDS))
                .map(value -> {
                    System.out.println(Thread.currentThread().getName());
                    return value * 10;
                })
                .subscribe(value -> System.out.println("Interval value:" + value));
        System.out.println("It disposable:" + subscribe.isDisposed());
        Thread.sleep(10000);
        subscribe.dispose();
        System.out.println("It disposable:" + subscribe.isDisposed());
    }

    private String value = "Hello";

    /**
     * Defer, just like in Rx, even having the Flux created the value to emmit in the pipeline
     * is calculated once the Flux is subscribed.
     */
    @Test
    public void defer() {
        Flux<String> flux = Flux.defer(() -> Flux.just(value));
        value = "Hello reactive world";
        flux.subscribe(value -> System.out.println("Item with updated value:" + value));
    }

    /**
     * ReactorCreating it´s well integrated with Java 8 Stream allowing you to get the stream and process in the Flux pipeline.
     * Every item emitted in the stream it will be emitted also in the Flux pipeline.
     */
    @Test
    public void fromStream() {
        Stream<String> stream = Arrays.asList("hello", "reactive", "reactive", "spring", "world").stream()
                .map(String::toUpperCase)
                .distinct();
        Flux.fromStream(stream)
                .doOnNext(value -> System.out.println("Stream value:" + value))
                .subscribe();
    }

    /**
     * Also it´s possible to create a Flux from an iterable in case want to use any type of collection.
     */
    @Test
    public void fromIterable() {
        Flux.fromIterable(Arrays.asList("hello", "old", "rx", "world"))
                .map(value -> value.replace("old", "reactive"))
                .map(value -> value.replace("rx", "spring"))
                .map(String::toUpperCase)
                .subscribe(System.out::println);
    }

    /**
     * Just get and emmit the first publish element in the pipeline.
     */
    @Test
    public void first() {
        Flux.first(Flux.just(1),
                Flux.just(3),
                Flux.just(4),
                Flux.just(5))
                .subscribe(System.out::println);
    }

    /**
     * Create a range of numbers and emmit all of them through the pipeline.
     */
    @Test
    public void range() {
        Flux.range(1, 10)
                .subscribe(System.out::println);
    }

    LinkedBlockingQueue<String> users = new LinkedBlockingQueue();

    /**
     * Another use of Flux is to make broadcast in case you need it.
     */
    @Test
    public void testBroadcast() {
        broadcast("Phil");
        broadcast("Paul");
        broadcast("Johnny");
        broadcast("Mike");
    }

    private void broadcast(String user) {
        users.add(user);
        Flux.fromIterable(users)
                .filter(otherUsers -> !user.equals(otherUsers))
                .doOnNext(otherUser -> System.out.println("Here we can inform other users"))
                .subscribe(value -> System.out.println("On next callback: " + value),
                        t -> System.out.println("On error callback: " + t),
                        () -> System.out.println("On complete callback"));
    }

    int count = 0;

    @Test
    public void checkIfItDisposable() throws InterruptedException {
        Disposable subscribe = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .map(number -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return number;
                }).subscribeOn(Schedulers.newElastic("1"))
                .subscribe();

        while (!subscribe.isDisposed() && count < 100) {
            Thread.sleep(400);
            count++;
            System.out.println("Waiting......");
        }
        System.out.println("It disposable:" + subscribe.isDisposed());
    }

    @Test
    public void checkIfItDisposableBlocking() {
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .map(number -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return number;
                }).subscribeOn(Schedulers.newElastic("1"))
                .blockLast(Duration.of(60, ChronoUnit.SECONDS));
        System.out.println("It disposable");
    }

    @Test
    public void fluxPipeline() {
        Flux.just("hello", "reactive", "Spring", "foo", null)
                .filter(word -> word != "foo") // filter
                .delayElements(Duration.ofMillis(100)) // Give a break, async
                .map(word -> word.toUpperCase()) // Transformation
                .flatMap(word -> Flux.just("-") //Composition
                        .map(item -> word.concat(item)))
                .onErrorResume(throwable -> Flux.just("Error because:" + throwable))//Error handling
                .subscribe(value -> System.out.println("On next function: " + value),
                        t -> System.out.println("On error function: " + t),
                        () -> System.out.println("On complete function"));

    }

}
