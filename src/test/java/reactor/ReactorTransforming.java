package reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;


public class ReactorTransforming {


    /**
     * FlatMap operator together with map are the most famous tranformation operator in functional programing.
     * An object that can implement flatMap it´s consider an Monad. And basically it´s used to chain functions.
     * In this example we combine three functions one embedded in the next one in order to combine the results.
     */
    @Test
    public void flatMap() {
        Flux.just("hello")
                .flatMap(value -> Flux.just("flatMap")
                        .map(newValue -> value.concat(" ").concat(newValue))
                        .flatMap(lastValue -> Flux.just("operator")
                                .map(newValue -> lastValue.concat(" ").concat(newValue))))
                .map(String::toUpperCase)
                .subscribe(System.out::println);
    }

    /**
     * Scan operator combine elements emitted in the pipeline. It´s defined by a initial structure to gather the elements,
     * And a Bifunction where we receive the element where we´re gathering the items and the new item.
     * That function must return the collection of elements.
     */
    @Test
    public void scan() {
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .scan(new ArrayList<Integer>(), (list, newValue) -> {
                    list.add(newValue);
                    return list;
                })
                .subscribe(System.out::println);
    }

    /**
     * Compose operator it´s mean to be used to transform one Flux into another with all the steps that want to define.
     *
     */
    @Test
    public void compose() throws InterruptedException {
        Scheduler mainThread = Schedulers.newElastic("1");
        Flux.just(("old element"))
                .compose(element ->
                        Flux.just("new element in new thread")
                                .publishOn(Schedulers.newElastic("2"))
                                .doOnNext(value -> System.out.println("Thread:" + Thread.currentThread().getName())))
                .publishOn(mainThread)
                .doOnNext(value -> System.out.println("Thread:" + Thread.currentThread().getName()))
                .subscribe(System.out::println);
        Thread.sleep(1000);
    }


}
