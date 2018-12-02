import io.reactivex.Observable;
import io.reactivex.internal.operators.flowable.FlowableFromObservable;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.Objects;

/**
 * Since ReactiveX implement with version 2.0 Reactive Stream API, nowadays is possible combine Monads from [ReactiveX] with
 * [Reactor] monads and the other way around.
 */
public class ReactiveMix {


    /**
     * In this example we create an [Observable] publisher, we wrap first into a Flowable, and then we are able to pass
     * this publisher as Flux publisher using [from] operator.
     */
    @Test
    public void observableIntoReactor() {
        System.out.println("----------------------------------------");
        Observable<String> observable =
                Observable.just("hello", "reactive", "Reactor", "and", "ReactiveX", "world")
                        .filter(Objects::nonNull)
                        .map(word -> "Rx " + word)
                        .map(String::toUpperCase);

        FlowableFromObservable<String> flowableFromObservable = new FlowableFromObservable(observable);

        Flux<String> flux = Flux.from(flowableFromObservable)
                .filter(Objects::nonNull)
                .map(word -> "Reactor and " + word)
                .flatMap(word -> Flux.just("_").map(word::concat));

        flux.subscribe(System.out::println, System.out::println);
        System.out.println("****************************************");
        observable.subscribe(System.out::println, System.out::println);
    }

    /**
     * In this example we create a [Flux] publisher, we dont have to wrap first is possible to pass directly
     * as a Observable publisher using [fromPublisher] operator.
     */
    @Test
    public void reactorToObservable() {
        System.out.println("----------------------------------------");
        Flux<String> flux = Flux.just("hello", "reactive", "Reactor", "and", "ReactiveX", "world")
                .filter(Objects::nonNull)
                .map(word -> "Reactor " + word)
                .flatMap(word -> Flux.just("_").map(word::concat));

        Observable<String> observable =
                Observable.fromPublisher(flux)
                        .filter(Objects::nonNull)
                        .map(word -> "Rx and " + word)
                        .map(String::toUpperCase);

        observable.subscribe(System.out::println, System.out::println);
        System.out.println("****************************************");
        flux.subscribe(System.out::println, System.out::println);
    }

}
