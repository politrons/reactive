package reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Just like ReactiveX by default the pipelines run synchronously, apart of some operators as interval, delay or others
 * that by default run asynchronously.
 * If we need to run into another thread we can use subscribeOn and publishOn.
 */
public class ReactorAsync {


    /**
     * SubscribeOn just like in Rx make the pipeline run asynchronously, from the beginning to the end.
     * <p>
     * In this example we get the three Flux pipelines and we run async all of them.
     * Once they are finish we zip the results in the order we establish in the operator.
     */
    @Test
    public void subscribeOn() throws InterruptedException {
        Scheduler scheduler = Schedulers.newElastic("thread");
        Scheduler scheduler1 = Schedulers.newElastic("thread");
        Scheduler scheduler2 = Schedulers.newElastic("thread");

        Flux<String> flux1 = Flux.just("hello ")
                .doOnNext(value -> System.out.println("Value " + value + " on :" + Thread.currentThread().getName()))
                .subscribeOn(scheduler);
        Flux<String> flux2 = Flux.just("reactive")
                .doOnNext(value -> System.out.println("Value " + value + " on :" + Thread.currentThread().getName()))
                .subscribeOn(scheduler1);
        Flux<String> flux3 = Flux.just(" world")
                .doOnNext(value -> System.out.println("Value " + value + " on :" + Thread.currentThread().getName()))
                .subscribeOn(scheduler2);
        Flux.zip(flux1, flux2, flux3)
                .map(tuple3 -> tuple3.getT1().concat(tuple3.getT2()).concat(tuple3.getT3()))
                .map(String::toUpperCase)
                .subscribe(value -> System.out.println("zip result:" + value));
        Thread.sleep(1000);

    }

    /**
     * publishOn operator make the pipeline run asynchronously after being used. Which means that all previous
     * steps in the pipeline it will be executed in the main thread, and after set the operator, the rest step
     * it will be executed in the thread that you specify
     */
    @Test
    public void publishOn() throws InterruptedException {
        Scheduler scheduler = Schedulers.newElastic("thread");
        Flux.just("Hello", "async", "world")
                .doOnNext(value -> System.out.println("current thread:" + Thread.currentThread().getName()))
                .publishOn(scheduler)
                .doOnNext(value -> System.out.println("after publishOn thread:" + Thread.currentThread().getName()))
                .subscribe();
    }

}
