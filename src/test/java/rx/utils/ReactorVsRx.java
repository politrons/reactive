package rx.utils;

import org.junit.Test;
import reactor.core.publisher.Flux;
import rx.Observable;

import java.lang.reflect.Method;
import java.util.List;


/**
 * This class is meant to be used as constantClass comparator between RxJava and Spring Reactor
 */
public class ReactorVsRx {

    @Test
    public void operators() {
        List<String> rxOperatorsList = Observable.from(Observable.class.getMethods())
                  .filter(m -> Observable.class.isAssignableFrom(m.getReturnType()))
                  .map(Method::getName)
                  .distinct()
                  .toSortedList().toBlocking().first();

        List<String> reactorOperatorsList = Flux.fromArray(Flux.class.getMethods())
                                            .filter(m -> Flux.class.isAssignableFrom(m.getReturnType()))
                                            .map(Method::getName)
                                            .distinct()
                                            .collectList().block();

        System.out.println(String.format("RX OPERATORS Nº:%s VS REACTOR OPERATORS Nº:%s",rxOperatorsList.size(), reactorOperatorsList.size()));

        rxOperators();
        reactorOperators();
    }

    @Test
    public void rxOperators() {
        Observable.from(Observable.class.getMethods())
                  .filter(m -> Observable.class.isAssignableFrom(m.getReturnType()))
                  .map(Method::getName)
                  .distinct()
                  .toSortedList()
                  .doOnNext(list -> System.out.println("RX OPERATORS:"+list.size()))
                  .subscribe(System.out::println);

    }

    @Test
    public void reactorOperators() {
        Flux.fromArray(Flux.class.getMethods())
            .filter(m -> Flux.class.isAssignableFrom(m.getReturnType()))
            .map(Method::getName)
            .distinct()
            .collectList()
            .doOnNext(list -> System.out.println("REACTOR OPERATORS:"+list.size()))
            .subscribe(System.out::println);

    }

}
