package rx.utils;

import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;


public class ObservableMonad {

    private Function<String, String> asyncFunc = new AsyncFunction();
    private Function<String, String> syncFunc = new SyncFunction();


    @Test
    public void testMonad() {
        runMonad("Async", asyncFunc);
        runMonad("Sync", syncFunc);
    }

    /**
     * This monad should never be modify, only the function that run in the pipeline.
     *
     * @param id
     * @param function
     */
    private void runMonad(String id, Function<String, String> function) {
        Observable.just(function)
                .flatMap(f -> Observable.just(f.apply(id))
                        .doOnNext(val -> System.out.println(Thread.currentThread().getName()))
                        .subscribeOn(Schedulers.newThread()))//This step it will async
                .map(String::toUpperCase)
                .subscribe(System.out::println);
    }


    /**
     * A function which internally get constantClass Promise from an external resource
     */
    public class AsyncFunction implements Function<String, String> {
        @Override
        public String apply(String id) {
            try {
                return getAsyncValue(id).get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class SyncFunction implements Function<String, String> {
        @Override
        public String apply(String id) {
            return "Hello world:".concat(id);
        }
    }

    private CompletableFuture<String> getAsyncValue(String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello world:".concat(id);
        });

    }
}
