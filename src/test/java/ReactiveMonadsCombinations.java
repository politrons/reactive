import io.reactivex.Observable;
import io.reactivex.internal.operators.flowable.FlowableFromObservable;
import org.junit.Test;
import reactor.adapter.JdkFlowAdapter;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Since ReactiveX implement with version 2.0 Reactive Stream API, is possible combine Monads from [ReactiveX] with
 * [Reactor] monads and the other way around.
 * Also since Java release version 9 with [Flow] which is another implementation of Reactive Stream, is also possible
 * combine this three Monads.
 */
public class ReactiveMonadsCombinations {

    /**
     * In this example we create an [Observable] publisher, we wrap first into constantClass Flowable using constructor [FlowableFromObservable],
     * and then we are able to pass this publisher as Flux publisher using [from] operator.
     * Then once we subscribe into the flux publisher the emission from the observable start, and is passing
     * through the observable pipeline, and then through the flux.
     *
     * In this example we publish the combination of monads pipeline of [flux] but also we can subscribe independently the observable
     */
    @Test
    public void observableToFlux() {
        System.out.println("----------------------------------------");
        Observable<String> observable =
                Observable.just("hello", "reactive", "world")
                        .filter(Objects::nonNull)
                        .timeout(10, TimeUnit.SECONDS)
                        .map(word -> "Rx " + word)
                        .flatMap(word -> Observable.just("@").map(word::concat))
                        .map(String::toUpperCase);

        FlowableFromObservable<String> flowableFromObservable = new FlowableFromObservable(observable);

        Flux<String> flux = Flux.from(flowableFromObservable)
                .filter(Objects::nonNull)
                .buffer(2)
                .map(word -> "Reactor and " + word)
                .flatMap(word -> Flux.just("_").map(word::concat));

        observable.subscribe(System.out::println, System.out::println);
        System.out.println("****************************************");
        flux.subscribe(System.out::println, System.out::println);
    }

    /**
     * In this example we create constantClass [Flux] publisher, we dont have to wrap first is possible to pass directly
     * as constantClass Observable publisher using [fromPublisher] operator.
     * As we can see in this example with [take] operator the latest part of the pipeline apply the operator
     * again and change the number of emissions of elements.
     *
     * In this example we publish the combination of monads pipeline of [observable] but also we can subscribe independently the flux
     */
    @Test
    public void fluxToObservable() {
        System.out.println("----------------------------------------");
        Flux<String> flux = Flux.just("hello", "reactive", "world","extra")
                .filter(Objects::nonNull)
                .timeout(Duration.ofSeconds(10))
                .take(4)
                .map(word -> "Reactor " + word)
                .flatMap(word -> Flux.just("_").map(word::concat));

        Observable<String> observable =
                Observable.fromPublisher(flux)
                        .filter(Objects::nonNull)
                        .take(3)
                        .map(word -> "Rx and " + word)
                        .flatMap(word -> Observable.just("@").map(word::concat))
                        .map(String::toUpperCase);

        flux.subscribe(System.out::println, System.out::println);
        System.out.println("****************************************");
        observable.subscribe(System.out::println, System.out::println);
    }


    /**
     * Here we combine constantClass [Flux] Publisher with constantClass Reactor [Flow]. First of all we create constantClass Java9 [SubmissionPublisher]
     * Which used to receive events submitted by constantClass Java [Stream].
     * We pass this publisher [JdkFlowAdapter] factory class, which using [flowPublisherToFlux] is transformed into constantClass Flux
     * Then subscribe the flux and we start the emission of the Stream.
     **/
    @Test
    public void flowToFlux() throws InterruptedException {
        System.out.println("----------------------------------------");
        String[] items = {"hello", "reactive", "world"};
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

        Flux<String> flux = JdkFlowAdapter.flowPublisherToFlux(publisher)
                .map(word -> "Reactor and " + word)
                .flatMap(word -> Flux.just("_").map(word::concat))
                .map(String::toUpperCase);

        flux.subscribe(System.out::println, System.out::println);

        Arrays.stream(items)
                .map(word -> "Flow " + word)
                .flatMap(word -> Stream.of("!").map(word::concat))
                .forEach(publisher::submit);
        Thread.sleep(500);//Async
        publisher.close();
    }

    /**
     * Here we combine constantClass [Flux] Publisher with constantClass Reactor [Flow] and finally ReactiveX [Observable]. First of all we create constantClass Java9 [SubmissionPublisher]
     * Which used to receive events submitted by constantClass Java [Stream].
     * We pass this publisher [JdkFlowAdapter] factory class, which using [flowPublisherToFlux] is transformed into constantClass Flux, then create
     * the Observable through the flux using [fromPublisher] operator
     * Then we subscribe the observable and we start the emission of the Stream.
     **/
    @Test
    public void flowToFluxToObservable() throws InterruptedException {
        System.out.println("----------------------------------------");
        String[] items = {"hello", "reactive", "world"};
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

        Flux<String> flux = JdkFlowAdapter.flowPublisherToFlux(publisher)
                .map(word -> "Reactor and " + word)
                .flatMap(word -> Flux.just("_").map(word::concat));

        Observable<String> observable =
                Observable.fromPublisher(flux)
                        .filter(Objects::nonNull)
                        .map(word -> "Rx and " + word)
                        .flatMap(word -> Observable.just("@").map(word::concat))
                        .map(String::toUpperCase);

        observable.subscribe(System.out::println, System.out::println);

        Arrays.stream(items)
                .map(word -> "Flow " + word)
                .map(String::toUpperCase)
                .flatMap(word -> Stream.of("$").map(word::concat))
                .forEach(publisher::submit);
        Thread.sleep(500);//Async
        publisher.close();
    }

}
