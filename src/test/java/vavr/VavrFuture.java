package vavr;

import io.vavr.concurrent.Future;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VavrFuture {

    @Test
    public void simpleFuture() {
        Future<Void> future = Future.run(() -> System.out.println("Hello world in " + Thread.currentThread().getName()));
        future.await();
    }

    @Test
    public void simpleFutureOnExecutor() {
        ExecutorService executor = Executors.newFixedThreadPool(100);
        Future<Void> future = Future.run(executor, () -> System.out.println("Hello world in " + Thread.currentThread().getName()));
        future.await();
    }

    @Test
    public void flatMapFuture() {
        Future<String> future = Future.of(() -> "Hello world in ")
                .flatMap(sentence -> Future.of(() -> sentence + Thread.currentThread().getName()))
                .map(String::toUpperCase);
        System.out.println(future.await().get());
    }

}
