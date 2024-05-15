package java22;

import org.junit.jupiter.api.Test;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

public class FeaturesJava22 {


    /**
     * Version 21 of Java add good new operators to interact with the data.
     * Now we can get first and last elements of collections as Scala does with [head] and [tail].
     * We can also reverse the order of elements.
     */
    @Test
    public void sequenceCollection() {
        List<String> collection = List.of("hello", "new", "collection", "features");
        System.out.println(collection.getFirst());
        System.out.println(collection.getLast());
        System.out.println(collection.stream().map(String::toUpperCase).toList());
        System.out.println(collection.reversed());
    }

    record User(String name, String password) {
    }

    record Account(int amount) {
    }

    /**
     * Since Java 21 [Pattern matching] it's getting better and getting close what scala can do.
     * Now we can evaluate object type to any data type defined in the switch and reference to variable name.
     * Or even unbox the content to the type in specific variable for each of them.
     * And from Java 22 even use unnamed variables.
     */
    @Test
    public void patternMatching() {
        runPatternMatchingUnboxing(new User("Politrons", "foo"));
        runPatternMatchingUnboxing(new Account(1000));
        runPatternMatching(new User("Politrons", "foo"));
        runPatternMatching(new Account(1000));
    }

    private void runPatternMatching(Object o) {
        switch (o) {
            case User u -> System.out.println(STR."Welcome \{u.name}");
            case Account a -> System.out.println(STR."You current amount is \{a.amount}");
            default -> throw new IllegalStateException(STR."Unexpected value: \{o}");
        }
    }

    private void runPatternMatchingUnboxing(Object o) {
        switch (o) {
            case User(String name, _) -> System.out.println(STR."Welcome \{name}");
            case Account(int amount) -> System.out.println(STR."You current amount is \{amount}");
            default -> throw new IllegalStateException(STR."Unexpected value: \{o}");
        }
    }

    /**
     * Since Java 21 Virtual Threads are quite mature enough to be used in our code base in case
     * we're not using any external lib that internally already use them.
     * API has no change since incubator state, and is as simple as invoke API
     * [Thread.ofVirtual()] to inform JVM we dont want to use an OS thread, but virtual.
     * Once Virtual Thread is created, you can decide to append the Runnable logic as eager [start]
     * or lazy [unstarted]
     *
     */
    @Test
    public void virtualThreads() throws InterruptedException {
        var vt1 = createVirtualThread();
        var vt2 = createVirtualThread();
        var vt3 = createVirtualThread();
        System.out.println(vt1.isVirtual());
        vt1.join();
        vt2.join();
        vt3.join();
    }

    private static Thread createVirtualThread() {
        return Thread.ofVirtual().name("MyVirtualThread").start(() -> {
            System.out.println(Thread.currentThread());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * In case you want to postpone the execution of one Virtual thread as lazy, you can
     * append the runnable using [unstarted], and once you want to be evaluated you can
     * use [start]
     */
    @Test
    public void lazyVirtualThreads() throws InterruptedException {
        var lazyVirtualThread = Thread.ofVirtual().name("MyVirtualThread").unstarted(() -> {
            System.out.println(Thread.currentThread());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread.sleep(500);
        System.out.println("Nothing happen now");
        lazyVirtualThread.start();
        lazyVirtualThread.join();
    }

    /**
     * Make composition of multiple Virtual Threads like FlatMap it would be as simple
     * as define and join each Virtual thread inside each other.
     */
    @Test
    public void composingVirtualThreads() throws InterruptedException {
        composeVirtualThread();
    }

    private static void composeVirtualThread() throws InterruptedException {
        Thread.ofVirtual().name("MyVirtualThread1").start(() -> {
            System.out.println(STR."Running async logic in \{Thread.currentThread()}");
            try {
                Thread.sleep(1000);
                Thread.ofVirtual().name("MyVirtualThread2").start(() -> {
                    System.out.println(STR."Running async logic in \{Thread.currentThread()}");
                    try {
                        Thread.sleep(1000);
                        Thread.ofVirtual().name("MyVirtualThread3").start(() -> {
                            System.out.println(STR."Running async logic in \{Thread.currentThread()}");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }).join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).join();
    }

    /**
     * Finally in Java 22 we have similar feature we have in Scala for more than a decade,
     * which is the possibility to _  a variable that for some reason it's not needed to be used at that time.
     */
    @Test
    public void unnamedVariables() {
        record A() {
        }
        var _ = new A();
        for (int _ : List.of(1, 2, 3)) {
            System.out.println("Just an iteration, variable not used");
        }
    }

    /**
     * String templates has been improving during the last versions, now introduce a new feature to template
     * variables inside a String, directly using the variable name inside the String.
     * We have to use [Interpolate] class STR as util class to being able to specif the variable using
     * [\{your_variable_name}]
     */
    @Test
    public void stringTemplate() {
        var quote = "Life is too short to last long.";
        System.out.println(STR."I love this Blink 182 quote. \{quote}");
    }

    /**
     * Java 22 Introduce [Gather] operator to allow multiple Stream process. One of those options is to
     * use [fold] to accumulate elements from a collection, and return that value.
     */
    @Test
    public void streamGathersFold() {
        Stream.of(1, 2, 3, 4, 5)
                .gather(Gatherers.fold(() -> 0, (item, accumulator) -> {
                    accumulator += item;
                    return accumulator;
                })).forEach(System.out::println);

    }

    /**
     * Another great feature of version 22 is [StructuredTaskScope] which allow async computation in parallel for multiple tasks
     * we need to create a [StructuredTaskScope] with a Strategy in case of any task fail, and then use the [scope] variable
     * created, to run new Virtual Thread executions for each of them using [fork] operator.
     * Once all task are defined as [Subtask] we can use [join] operator to wait for all of them to finish.
     * And then use [get] to get the output for each of them.
     */
    @Test
    public void structureConcurrency() throws InterruptedException, ExecutionException {
        try(var scope = new StructuredTaskScope.ShutdownOnFailure()){
            StructuredTaskScope.Subtask<String> hello = scope.fork(() -> {
                Thread.sleep(new Random().nextInt(1000));
                System.out.println(STR."Running on thread \{Thread.currentThread()}");
                return "hello";
            });
            StructuredTaskScope.Subtask<String> world = scope.fork(() -> {
                Thread.sleep(new Random().nextInt(1000));
                System.out.println(STR."Running on thread \{Thread.currentThread()}");
                return "world";
            });
            scope.join().throwIfFailed();
            System.out.println(STR."\{hello.get()} \{world.get()}");

        }
    }

    /**
     * We can also handle the side-effect of [StructuredTaskScope] if we use operator [exception] once we join the tasks.
     * It will return an [Optional] type of Throwable.
     */
    @Test
    public void structureConcurrencyCaptureSideEffect() throws InterruptedException, ExecutionException {
        try(var scope = new StructuredTaskScope.ShutdownOnFailure()){
            StructuredTaskScope.Subtask<String> hello = scope.fork(() -> {
                System.out.println(STR."Running on thread \{Thread.currentThread()}");
                if(new Random().nextBoolean()){
                    throw new IllegalStateException("This task smell fishy");
                }
                return "hello";
            });
            StructuredTaskScope.Subtask<String> world = scope.fork(() -> {
                System.out.println(STR."Running on thread \{Thread.currentThread()}");
                if(new Random().nextBoolean()){
                    throw new IllegalStateException("This task smell fishy");
                }
                return "world";
            });
            var maybeSideEffect = scope.join().exception();
            if(maybeSideEffect.isPresent()){
                System.out.println(STR."Task did not finish because side effect \{maybeSideEffect.get()}");
            }else{
                System.out.println(STR."\{hello.get()} \{world.get()}");
            }

        }
    }

}


