package rx.observables.scheduler;

import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action1;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * @author Pablo Perez
 *         Using the option subscribeOn or observerOn, you specify in your observable that you want to execute all/some pipeline steps into another thread,
 *         making the pipeline asyncronious
 */
public class ObservableAsynchronous {

    int total = 0;

    Integer[] numbers = {0, 1, 2, 3, 4};

    //************************DIFFERENCE BETWEEN subscribeOn AND observerOn***********************\\

    /**
     * Once that you set in your pipeline the observerOn all the next steps of your pipeline will be executed in another thread.
     * Shall print
     * First step main
     * Second step RxNewThreadScheduler-2
     * Third step RxNewThreadScheduler-1
     */
    @Test
    public void testObservableObserverOn() throws InterruptedException {
        Subscription subscription = Observable.just(1)
                .doOnNext(number -> System.out.println("First step " + Thread.currentThread()
                        .getName()))
                .observeOn(Schedulers.newThread())
                .doOnNext(number -> System.out.println("Second step " + Thread.currentThread()
                        .getName()))
                .observeOn(Schedulers.newThread())
                .doOnNext(number -> System.out.println("Third step " + Thread.currentThread()
                        .getName()))
                .subscribe();
        new TestSubscriber((Observer) subscription)
                .awaitTerminalEvent(100, TimeUnit.MILLISECONDS);
    }


    /**
     * Does not matter at what point in your pipeline you set your subscribeOn, once that is set in the pipeline,
     * all steps will be executed in another thread.
     * Shall print
     * First step RxNewThreadScheduler-1
     * Second step RxNewThreadScheduler-1
     */
    @Test
    public void testObservableSubscribeOn() throws InterruptedException {
        Subscription subscription = Observable.just(1)
                .doOnNext(number -> System.out.println("First step " + Thread.currentThread()
                        .getName()))
                .subscribeOn(Schedulers.newThread())
                .doOnNext(number -> System.out.println("Second step " + Thread.currentThread()
                        .getName()))
                .subscribe();
        new TestSubscriber((Observer) subscription)
                .awaitTerminalEvent(100, TimeUnit.MILLISECONDS);
    }

    /**
     * Combining subscribeOn and observerOn it´s possible, and one can take the control over  the other
     * In this example since we define observerOn, everything before this operator it will be executed in the observerOn thread defined,
     * After that, when we use the subscribeOn operator, the rest of the step it will be executed in the defined thread.
     *
     * @throws InterruptedException
     */
    @Test
    public void testObservableObservableOnAndSubscribeOn() throws InterruptedException {
        Subscription subscription = Observable.just(1)
                .doOnNext(number -> System.out.println("First step " + Thread.currentThread()
                        .getName()))
                .observeOn(Schedulers.newThread())
                .doOnNext(number -> System.out.println("Second step " + Thread.currentThread()
                        .getName()))
                .doOnNext(number -> System.out.println("Third step " + Thread.currentThread()
                        .getName()))
                .subscribeOn(Schedulers.newThread())
                .subscribe();
        new TestSubscriber((Observer) subscription)
                .awaitTerminalEvent(100, TimeUnit.MILLISECONDS);
    }

    /**
     * Combining subscribeOn and observerOn it´s possible, and one can override the other
     *
     * @throws InterruptedException
     */
    @Test
    public void testObservableSubscribeOnAndObserverOn() throws InterruptedException {
        Subscription subscription = Observable.just(1)
                .doOnNext(number -> System.out.println("First step " + Thread.currentThread()
                        .getName()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .doOnNext(number -> System.out.println("Second step " + Thread.currentThread()
                        .getName()))
                .subscribe();
        new TestSubscriber((Observer) subscription)
                .awaitTerminalEvent(100, TimeUnit.MILLISECONDS);
    }


    //************************DIFFERENCE BETWEEN ASYNC AND SYNC OBSERVABLE***************************\\

    /**
     * In this test we prove how when we subscribe constantClass observable using scheduler, this one is executed in another thread,
     * and total is in the scope of every thread.
     * Shall print
     * <p>
     * I finish before the observable finish.  Items emitted:0
     */
    @Test
    public void testObservableAsync() throws InterruptedException {
        Subscription subscription = Observable.from(numbers)
                .doOnNext(increaseTotalItemsEmitted())
                .subscribeOn(Schedulers.newThread())
                .subscribe(number -> System.out.println("Items emitted:" + total));
        System.out.println("I finish before the observable finish.  Items emitted:" + total);
        new TestSubscriber((Observer) subscription)
                .awaitTerminalEvent(100, TimeUnit.MILLISECONDS);
    }

    /**
     * In this test we prove how when we subscribe constantClass observable and we not use subscribeOn, this one is executed in the main thread.
     * And total is in the scope of both
     * Shall print
     * <p>
     * Items emitted:0
     * Items emitted:1
     * Items emitted:3
     * Items emitted:6
     * Items emitted:10
     * I finish after the observable finish.  Items emitted:10
     */
    @Test
    public void testObservableSync() {
        Observable.from(numbers)
                .doOnNext(increaseTotalItemsEmitted())
                .subscribe(number -> System.out.println("Items emitted:" + total));
        System.out.println("I finish after the observable finish.  Items emitted:" + total);
    }


    private Action1<Integer> increaseTotalItemsEmitted() {
        return number -> {
            try {
                total += number;
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    //***********************+DIFFERENCE BETWEEN SCHEDULERS ********************************\\

    /**
     * scheduler.io() implementation backed to thread-pool that will grow as needed. So,
     * we don’t know which thread will be used whenever we call Schedulers.io()
     */
    @Test
    public void io() {
        Observable.just(1, 2, 3, 4, 5)
                .subscribeOn(Schedulers.io())
                .subscribe(onNext);
        Observable.just(6, 7, 8, 9, 10)
                .subscribeOn(Schedulers.io())
                .subscribe(onNext);
    }

    /**
     * All jobs that subscribes on trampoline() will be queued and excuted one by one
     */
    @Test
    public void trampoline() {
        Observable.just(2, 4, 6, 8, 10)
                .subscribeOn(Schedulers.trampoline())
                .subscribe(onNext);
        Observable.just(1, 3, 5, 7, 9)
                .subscribeOn(Schedulers.trampoline())
                .subscribe(onNext);
    }

    Action1<Integer> onNext = integer -> System.out.println("Number = " + integer);


    /**
     * Using flatMap and subscribeOn we can execute an item per thread
     */
    @Test
    public void itemPerThread() {
        Observable.just(1, 2)
                .flatMap(item -> Observable.just(item)
                        .subscribeOn(Schedulers.newThread())
                        .doOnNext(i -> System.out.println("Thread:" + Thread.currentThread())))
                .subscribe(System.out::println);
    }

    @Test
    public void testBackToMainThread() throws InterruptedException {
        processValue(1);
        processValue(2);
        processValue(3);
        processValue(4);
        processValue(5);
        Thread.sleep(3000);
        while (tasks.size() != 0) {
            tasks.take().run();
        }
        System.out.println("done");
    }

    private LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();


    private void processValue(int value){
        Observable.just(value)
                .subscribeOn(Schedulers.io())
                .doOnNext(number -> processExecution())
                .observeOn(Schedulers.from(command -> tasks.add(command)))
                .subscribe(x -> System.out.println("Thread:" + Thread.currentThread().getName() + " value:" + x));
    }

    private void processExecution() {
        System.out.println("Execution in " + Thread.currentThread().getName());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
