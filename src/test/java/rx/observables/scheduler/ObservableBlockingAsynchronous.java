package rx.observables.scheduler;

import hu.akarnokd.rxjava2.schedulers.BlockingScheduler;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;


/**
 * @author Pablo Perez
 *         Using the option subscribeOn or observerOn, you specify in your observable that you want to execute all/some pipeline steps into another thread,
 *         making the pipeline asyncronious
 */
public class ObservableBlockingAsynchronous {

    @Test
    public void Blocking() {
        BlockingScheduler scheduler = new BlockingScheduler();
        scheduler.execute(() -> Flowable.range(1, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(scheduler)
                .doAfterTerminate(scheduler::shutdown)
                .subscribe(v -> System.out.println(v + " on " + Thread.currentThread())));

        System.out.println("BlockingScheduler finished");
    }


    private BlockingScheduler scheduler = new BlockingScheduler();

    @Test
    public void testBackToMainThread() throws InterruptedException {
        processValue(1);
        processValue(2);
        processValue(3);
        processValue(4);
        processValue(5);
        System.out.println("done");
    }

    private void processValue(int value) throws InterruptedException {
        scheduler.execute(() -> Flowable.just(value)
                .subscribeOn(Schedulers.io())
                .doOnNext(number -> processExecution())
                .observeOn(scheduler)
                .doAfterTerminate(scheduler::shutdown)
                .subscribe(v -> System.out.println(v + " on " + Thread.currentThread())));
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
