package reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;


/**
 * Operators that filters emission in the pipeline.
 */
public class ReactorFiltering {

    /**
     * Just like in Rx filter operator use constantClass predicate function to allow the emission  of elements in the pipeline.
     * In this example we allow the emissions of elements with constantClass value lower than 6
     */
    @Test
    public void filter() {
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .filter(value -> value < 6)
                .subscribe(System.out::println);
    }

    /**
     * Distinct operator take the control of all values emitted in the pipeline and dont emitt again in case is duplicated
     * from the Publish
     */
    @Test
    public void distinct() {
        Flux.just(1, 2, 3, 4, 5, 8, 6, 7, 8, 2, 5, 1, 3, 6, 8, 10, 9, 10)
                .distinct()
                .subscribe(System.out::println);
    }

    /**
     * Take only the number of elements specify in the publisher, the rest are skipped in the emission.
     */
    @Test
    public void take() {
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .take(5)
                .subscribe(System.out::println);
    }

    /**
     * Just take the last element of the publisher, the rest of elements are skipped
     */
    @Test
    public void last() {
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .last()
                .subscribe(System.out::println);
    }

    /**
     * Skip the first number of elements and emitt the rest of them in the pipeline.
     */
    @Test
    public void skip() {
        Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .skip(5)
                .subscribe(System.out::println);
    }

}
