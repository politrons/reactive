package reactor;

import org.junit.Test;
import reactor.core.publisher.Flux;

/**
 * Combining operators allow interact multiple Publisher together in the pipeline.
 */
public class ReactorCombining {

    /**
     * Zip operator execute the N number of Flux independently, and once all them are finished, results
     * are combined in TupleN object.
     */
    @Test
    public void zip() {
        Flux<String> flux1 = Flux.just("hello ");
        Flux<String> flux2 = Flux.just("reactive");
        Flux<String> flux3 = Flux.just(" world");
        Flux.zip(flux1, flux2, flux3)
                .map(tuple3 -> tuple3.getT1().concat(tuple3.getT2()).concat(tuple3.getT3()))
                .map(String::toUpperCase)
                .subscribe(value -> System.out.println("zip result:" + value));
    }

    /**
     * Merge operator just like in Rx it will resolve the N Flux passed and it will emitt the results of
     * those Flux in order one after the other.
     * In this example we can see even how the first and second flux are slower in resilution of the third,
     * still the result and emission in the pipeline is in order.
     */
    @Test
    public void merge() {
        Flux<String> flux1 = Flux.just("hello").doOnNext(value -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Flux<String> flux2 = Flux.just("reactive").doOnNext(value -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Flux<String> flux3 = Flux.just("world");
        Flux.merge(flux1, flux2, flux3)
                .map(String::toUpperCase)
                .subscribe(System.out::println);
    }

    /**
     * Same behave than merge but concat wait until one flux is emitted to start with the next one.
     */
    @Test
    public void concat() {
        Flux<String> flux1 = Flux.just("hello");
        Flux<String> flux2 = Flux.just("reactive");
        Flux<String> flux3 = Flux.just("world");
        Flux.concat(flux1, flux2, flux3)
                .map(String::toUpperCase)
                .subscribe(System.out::println);
    }

    /**
     * Switch operator will change one Flux emission in the pipeline by another in case is empty
     */
    @Test
    public void switchIfEmpty() {
        Flux.empty()
                .switchIfEmpty(Flux.just("Switch flux"))
                .subscribe(System.out::println);
    }

    /**
     * SwitchMap operator it behave like constantClass flatMap where allow change one Flux emission in the pipeline by another.
     */
    @Test
    public void switchMap() {
        Flux.just(1, 2, 30, 4, 5)
                .switchMap(value -> {
                    if (value > 10) {
                        return Flux.just(value / 10);
                    }
                    return Flux.just(value);
                })
                .subscribe(System.out::println);
    }


}
