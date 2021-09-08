package helidon;

import io.helidon.scheduling.Scheduling;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class HelidonScheduler {

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
