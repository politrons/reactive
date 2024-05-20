package patterns;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class RacePattern {

    /**
     * With this pattern we can run multiple light async task in parallel using CompletableFuture, with Virtual threads.
     * Then we gather all of them using [CompletableFuture] operator [anyOf] together with [get] which means, we will
     * block this operation, until first one finish his task.
     * After that since we don't care about the results of the other tasks, we will cancel all of them to free resources.
     *
     * In this example we have this 3 task simulating a car race, where each of them they have a random 1s to finish the race.
     * We will get the car race that finish first the race, and we will cancel the execution of the remaining threads.
     */
    @Test
    public void raceBetweenAsyncTask() throws ExecutionException, InterruptedException {
        var executorService = Executors.newVirtualThreadPerTaskExecutor();

        var asyncTask1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(new Random().nextInt(1000));
                return STR."Porsche model:\{Thread.currentThread()}";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
        var asyncTask2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(new Random().nextInt(1000));
                return STR."Ferrari model:\{Thread.currentThread()}";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, executorService);

        var asyncTask3 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(new Random().nextInt(1000));
                return STR."Audi model:\{Thread.currentThread()}";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, executorService);

        var result = CompletableFuture.anyOf(asyncTask1, asyncTask2, asyncTask3).get();
        asyncTask1.cancel(true);
        asyncTask2.cancel(true);
        asyncTask3.cancel(true);
        System.out.println(STR."And the winner is... \{result}");

    }


}
