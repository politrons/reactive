package java19;

//import jdk.incubator.concurrent.StructuredTaskScope;
import io.vavr.concurrent.Promise;
import io.vavr.control.Try;
import org.junit.Test;

import java.lang.foreign.Linker;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Java19Features {


    /**
     * Java 19 allow us to create Collection wih a fixed size for the collection.
     */
    @Test
    public void collections() {
        var list = new ArrayList<String>(1);
        list.add("hello");
        list.add("world");
        System.out.println(list);
    }

    /**
     * Now With Java 19 we can use a more idiomatic syntax for pattern matching to add more filters
     * Instead of use [&] when can use [when] condition
     */
    @Test
    public void patterMatching() {
        switch ((Object) "Hello world") {
            case String s when s.length() > 50 -> System.out.println(s.toUpperCase());
            case String s when(s.contains("world") && s.length() > 5) ->
                    System.out.println(s.concat("!!!").toLowerCase());
            case Integer i -> System.out.println(i * i);
            default -> System.out.println("Nothing found");
        }
    }

    /**
     * Now also we can match and extract the values from a record class, just like Scala does.
     * Also, now it is possible add in the pattern matching nested records, so we can extract also
     * the value of those.
     */
    @Test
    public void patterMatchingWithRecordPattern() {
        switch ((Object) new User(new Name("Politrons"), 41)) {
            case User(Name(String name),Integer age) ->
                    System.out.printf("hello %s, happy %s birthday", name, age);
            default -> System.out.println("Nothing found");
        }
    }

    /**
     * Java 19 finally introduce Virtual Threads(Green Threads) a lightweight version of runnable task
     * created by JVM instead the Kernel of the OS.
     * This Virtual threads are easier to create/destroy and consume less resources.
     * Java finally has reuse the Thread factory class to include factory method [startVirtualThread]
     * to start a new Runnable running in this type of thread
     */
    @Test
    public void virtualThreads() throws InterruptedException {
        Thread virtualThread = Thread.startVirtualThread(() -> {
            System.out.printf("Hello world running in %s\n", Thread.currentThread());
            System.out.printf("Is Virtual. %s\n", Thread.currentThread().isVirtual());
            System.out.printf("ThreadId. %s\n", Thread.currentThread().threadId());
            System.out.printf("State. %s\n", Thread.currentThread().getState());

        });
        virtualThread.join();
    }

    /**
     * We can emulate composition of VirtualThreads by simply passing the values from
     * previous logic execution to the next runnable task executed in another Virtual Thread.
     */
    @Test
    public void virtualThreadsComposition() throws InterruptedException {
        Thread program = Thread.ofVirtual().start(()-> {
            System.out.printf("Running first task in %s\n", Thread.currentThread());
            var result = "hello";
            Thread.startVirtualThread(()-> {
                System.out.printf("Running second task in %s\n", Thread.currentThread());
                var result1 = result + "-world";
                System.out.println(result1);
            });
        });
        //Evaluate program
        program.join();
    }

    /**
     * Another good improvement is that now Java 19 include in [Executors] the option to create Executor that
     * create Virtual Threads to be used by Async task like [CompletableFuture] in order to use it, you just need
     * to use new API [newVirtualThreadPerTaskExecutor]
     */
    @Test
    public void virtualThreadsParallel() throws ExecutionException, InterruptedException {
        var task1=CompletableFuture.supplyAsync(()->"hello", Executors.newVirtualThreadPerTaskExecutor());
        var task2 = CompletableFuture.supplyAsync(()->"world", Executors.newVirtualThreadPerTaskExecutor());
        System.out.println(task1.get() + " " + task2.get());
    }

    /**
     * Incubator, not yet available.
      * API to run parallel task and join then
     */
//    @Test
//    public void structureTaskScope() throws InterruptedException, ExecutionException {
//        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
//            Future<String> task1 = scope.fork(() ->"Hello");
//            Future<String> task2 = scope.fork(() -> "World");
//            Future<String> task3 = scope.fork(() -> "!!!");
//
//            scope.join();
//            scope.throwIfFailed();
//
//            var result = task1.resultNow()
//                    .concat(task2.resultNow())
//                    .concat(task3.resultNow());
//            System.out.println(result);
//        }
//    }

    record User(Name name, Integer age) {
    }

    record Name(String value){}
}
