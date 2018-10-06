package java8;

import org.junit.Test;
import scala.util.Either;
import scala.util.Right;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The Java 8 promises for the futures.
 * The API allow callbacks to combine futures, as finally get the result in the whenComplete
 * callback operator.
 */
public class CompletableFutureFeature {


    /**
     * Create the promise with the supply function which return the value to be passed to the whenComplete once it´s
     * ready
     */
    @Test
    public void supplyAsync() throws InterruptedException {
        CompletableFuture<Either<Integer, String>> completableFuture = CompletableFuture.supplyAsync(this::getValue);
        completableFuture.whenComplete((result, throwable) -> System.out.println(result.right().get()));
        Thread.sleep(2000);
    }

    /**
     * You can trigger the promise using [[complete]] which will resolve the result in the same thread, and it will return a boolean
     * as an state of the future, true in case that the future finish or false
     */
    @Test
    public void complete() {
        CompletableFuture<Either<Integer, String>> completableFuture = new CompletableFuture<>();
        completableFuture.whenComplete((result, throwable) -> System.out.println(result.right().get()));
        boolean complete = completableFuture.complete(getValue());//Pass the value to return once he it.
        System.out.println(complete);
    }

    /**
     * You can trigger the promise using [[completeAsync]] which will resolve the result in another thread.
     */
    @Test
    public void completeAsync() throws InterruptedException {
        CompletableFuture<Either<Integer, String>> completableFuture = new CompletableFuture<>();
        completableFuture.thenRun(() -> System.out.println("Do on Next action " + Thread.currentThread().getName()));
        completableFuture.completeAsync(this::getValue);
        completableFuture.whenComplete((result, throwable) -> System.out.println(result.right().get() + " " + Thread.currentThread().getName()));
        Thread.sleep(2000);
    }

    /**
     * We have zip futures using operator thenCombine, which it will merge one promise value to the other one.
     * The Api allow combine so many as we need to the merge link can be infinite.
     */
    @Test
    public void zip() throws InterruptedException {
        CompletableFuture<Either<Integer, String>> completableFuture = CompletableFuture.supplyAsync(this::getValue);
        CompletableFuture<Either<Integer, String>> completableFuture1 = CompletableFuture.supplyAsync(this::getValue);
        CompletableFuture<Either<Integer, String>> completableFuture2 = CompletableFuture.supplyAsync(this::getValue);

        completableFuture
                .thenCombine(completableFuture1, (c1, c2) -> new Right<>(c1.right().get() + "|" + c2.right().get()))
                .thenCombine(completableFuture2, (c1, c2) -> new Right<>(c1.right().get() + "|" + c2.right().get()))

                .whenComplete((result, throwable) -> System.out.println(result.right().get()));
        Thread.sleep(2000);
    }

    /**
     * ThenApply it would behave like the map operator in Scala Future. It will mutate the item through the pipeline
     * to the whenComplete callback, once that finish
     */
    @Test
    public void thenApply() throws InterruptedException {
        CompletableFuture.supplyAsync(this::getValue)
                .thenApply(either -> {
                    String value = either.right().get().toUpperCase();
                    return new Right<Integer, String>(value);
                })
                .thenApply(either ->{
                    String value = either.right().get().substring(0,5);
                    return new Right<Integer, String>(value);
                })
                .whenComplete((result, throwable) -> System.out.println(result.right().get()));
        Thread.sleep(2000);
    }

    private Right<Integer, String> getValue() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Right<>(UUID.randomUUID().toString());
    }

}
