package helidon;

import io.helidon.scheduling.Scheduling;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class HelidonScheduler {

    /**
     * Scheduler, is just a Task executor that is running a task in a scheduler thread pool.
     * It allow you to specify how often this scheduler run the task, and also a delay execution
     * in the initial execution if you want.
     */
    @Test
    public void schedulerFeature() throws InterruptedException {
        Scheduling.fixedRateBuilder()
                .delay(4)
                .initialDelay(2)
                .timeUnit(TimeUnit.SECONDS)
                .task(inv -> {
                    System.out.println("Running in:" + Thread.currentThread().getName());
                    System.out.println("Every 4 seconds an action, with an initial delay");
                })
                .build();

        Thread.sleep(12000);
    }
}
