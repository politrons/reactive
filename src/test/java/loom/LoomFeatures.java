package loom;

import hu.akarnokd.rxjava3.fibers.FiberInterop;
import io.reactivex.rxjava3.core.Flowable;
import org.junit.Test;

/**
 * It require a loom build configure in your Intellij https://jdk.java.net/loom/
 */
public class LoomFeatures {

    //TODO:Intellij cannot compile this code yet
//    @Test
//    public void virtualThreadFeature() throws InterruptedException {
//        final Thread thread = Thread.startVirtualThread(() -> {
//            System.out.println("Hello World from " + Thread.currentThread().getThreadGroup().getName());
//        });
//        thread.start();
//        thread.join();
//    }


    /**
     * Using library from akarnokd we can use Project Loom virtual threads https://github.com/akarnokd/RxJavaFiberInterop
     * Here using [FiberInterop] [create] we are able to use a [FiberGenerator] that create a fiber (green thread,VirtualThread)
     * and in that callback receive a [FiberEmitter] to allow you emit an element in the Stream running that computation in the Virtual Thread.
     * <p>
     * Once we run in the Fiber, the whole stream making transformation(Map) or composition(flatMap) is also running in this Thread.
     */
    @Test
    public void rxFiberFeatures() {
        final Flowable<String> objectFlowable = FiberInterop.<String>create(emitter -> {
                    Thread.currentThread().setName("My Virtual Thread");
                    printVirtualThreadInfo();
                    emitter.emit("Hello world from Java Loom Virtual Threads");
                })
                .map(value -> {
                    printVirtualThreadInfo();
                    return value.toUpperCase();
                })
                .flatMap(value -> {
                    printVirtualThreadInfo();
                    return FiberInterop.create(emitter -> emitter.emit("$$$" + value + "$$$"));
                });
        final String result = objectFlowable.blockingFirst();
        System.out.println(result);
    }

    private void printVirtualThreadInfo() {
        System.out.println("Virtual Thread info");
        System.out.println("-------------------");
        System.out.println("Group: " + Thread.currentThread().getThreadGroup().getName());
        System.out.println("Name: " + Thread.currentThread().getName());
        System.out.println("State:" + Thread.currentThread().getState());
    }
}
