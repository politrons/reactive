package reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.ArrayList;

/**
 * Operators used to transform the elements emitted in the pipeline.
 */
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
     * Compose operator it´s mean to be used to transform one Flux into another.
     * It will affect the whole stream and not only what happens inside the operator as flatMap does.
     */
    @Test
    public void compose() throws InterruptedException {
        Flux.just(1, 2, 3, 4, 5)
                .compose(Flux::collectList)
                .doOnNext(value -> System.out.println("Thread:" + Thread.currentThread().getName()))
                .subscribe(System.out::println);
        Thread.sleep(1000);
    }

    @Test
    public void flatMapVsCompose() throws InterruptedException {
        Flux.just(1, 2, 3, 4, 5)
                .flatMap(Flux::just)
                .doOnNext(value -> System.out.println("Thread:" + Thread.currentThread().getName()))
                .subscribe(System.out::println);
        Thread.sleep(1000);
    }

    /**
     * Window operator transform a number of elements to be emitted in the pipeline in a number of groups
     * defined by the number of elements to gather in one emission.
     * In this case having 10 elements to be emit and specifying that we want to group 5 by 5, we would end up
     * having two Flux of 5 elements each.
     */
    @Test
    public void window() {
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .window(5)
                .flatMap(flux -> {
                    System.out.println("New Flux:" + flux);
                    return flux;
                })
                .subscribe(System.out::println);

    }

}
