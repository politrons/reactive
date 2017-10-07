package utils;

import org.junit.Test;
import scala.util.Either;
import scala.util.Right;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CompletableFutureFeature {

    @Test
    public void main() throws InterruptedException {
        CompletableFuture<Either<Integer, String>> completableFuture = new CompletableFuture<>();
        completableFuture.complete(getValue());
        completableFuture.whenComplete((result, throwable) -> {
            System.out.println(result.right().get());
        });
        Thread.sleep(5000);
    }

    @Test
    public void zip() throws InterruptedException {
        CompletableFuture<Either<Integer, String>> completableFuture = new CompletableFuture<>();
        CompletableFuture<Either<Integer, String>> completableFuture1 = new CompletableFuture<>();
        CompletableFuture<Either<Integer, String>> completableFuture2 = new CompletableFuture<>();


        CompletableFuture<Right> rightCompletableFuture = completableFuture
                .thenCombine(completableFuture1, (c1, c2) -> new Right<>(c1.right().get() + "|" + c2.right().get()));


        rightCompletableFuture.whenComplete((result, throwable) -> {
            System.out.println(result.right().get());
        });


        completableFuture.complete(getValue());
        completableFuture1.complete(getValue());
        completableFuture2.complete(getValue());

        Thread.sleep(5000);
    }

    private Right<Integer, String> getValue() throws InterruptedException {
        Thread.sleep(1000);
        return new Right<>(UUID.randomUUID().toString());
    }

}
