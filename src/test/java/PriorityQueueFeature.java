import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

public class PriorityQueueFeature {

    @Test
    public void priorityQueue() throws InterruptedException {

        /* ──────────────────────────────────────────────────────────────────
         * 1) Three dedicated ThreadPools, one per priority
         * ────────────────────────────────────────────────────────────────── */
        ExecutorService highPool = Executors.newFixedThreadPool(
                50, Thread.ofVirtual().name("high-", 0).factory());

        ExecutorService mediumPool = Executors.newFixedThreadPool(
                30, Thread.ofVirtual().name("medium-", 0).factory());

        ExecutorService lowPool = Executors.newFixedThreadPool(
                20, Thread.ofVirtual().name("low-", 0).factory());

        /* ──────────────────────────────────────────────────────────────────
         * 2) Statistics containers, one per priority
         * ────────────────────────────────────────────────────────────────── */
        Stats highStats   = new Stats();
        Stats mediumStats = new Stats();
        Stats lowStats    = new Stats();

        /* ──────────────────────────────────────────────────────────────────
         * 3) Enqueue 10 000 tasks with the requested distribution
         * ────────────────────────────────────────────────────────────────── */
        int totalTasks        = 10_000;
        int highQuota   = (int) (totalTasks * 0.50);   // 50 %
        int mediumQuota = (int) (totalTasks * 0.30);   // 30 %

        for (int i = 0; i < totalTasks; i++) {
            long enqueueTime = System.nanoTime(); // capture time **before** submission
            Runnable task = getLogicTask(enqueueTime, highStats, mediumStats, lowStats);
            /* Assign the task to the correct pool based on the loop index */
            if (i < highQuota) {
                highPool.execute(wrap(task, "high"));
            } else if (i < highQuota + mediumQuota) {
                mediumPool.execute(wrap(task, "medium"));
            } else {
                // low quota + remainder both go to the low-priority pool
                lowPool.execute(wrap(task, "low"));
            }
        }

        /* ──────────────────────────────────────────────────────────────────
         * 4) Shut down pools and wait for completion
         * ────────────────────────────────────────────────────────────────── */
        highPool.shutdown();
        mediumPool.shutdown();
        lowPool.shutdown();

        highPool.awaitTermination(5, TimeUnit.SECONDS);
        mediumPool.awaitTermination(5, TimeUnit.SECONDS);
        lowPool.awaitTermination(5, TimeUnit.SECONDS);

        /* ──────────────────────────────────────────────────────────────────
         * 5) Print average latency in milliseconds per priority group
         * ────────────────────────────────────────────────────────────────── */
        System.out.printf("High   - average latency: %.3f ms%n", highStats.getAverageMillis());
        System.out.printf("Medium - average latency: %.3f ms%n", mediumStats.getAverageMillis());
        System.out.printf("Low    - average latency: %.3f ms%n", lowStats.getAverageMillis());

    }

    private static Runnable getLogicTask(long enqueueTime, Stats highStats, Stats mediumStats, Stats lowStats) {
        return () -> {
            // simulate some work –– replace with real logic if needed
            doBusyWork();
            long latencyNanos = System.nanoTime() - enqueueTime;
            // update the corresponding statistics
            if (Thread.currentThread().getName().contains("high")) {
                highStats.addSample(latencyNanos);
            } else if (Thread.currentThread().getName().contains("medium")) {
                mediumStats.addSample(latencyNanos);
            } else {
                lowStats.addSample(latencyNanos);
            }
        };
    }


    /** Simple holder for statistics per priority group */
    private static final class Stats {
        final LongAdder totalNanos = new LongAdder();
        final LongAdder count      = new LongAdder();

        void addSample(long nanos) {
            totalNanos.add(nanos);
            count.increment();
        }

        double getAverageMillis() {
            return count.sum() == 0
                    ? 0
                    : (totalNanos.doubleValue() / 1_000_000.0) / count.doubleValue();
        }

    }

    /** Adds a human-readable suffix to the worker thread names */
    private static Runnable wrap(Runnable delegate, String tag) {
        return delegate::run;
    }

    /** Fake workload: ~1 ms busy spin (replace as needed) */
    private static void doBusyWork() {
        long target = System.nanoTime() + 1_000_000; // 1 ms
        while (System.nanoTime() < target) {
            // busy wait
        }
    }

}
