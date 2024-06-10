package netflix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import org.junit.Test;

public class HystrixCircuitBreaker {

    @Test
    public void runService(){
        for (int i = 0; i < 10; i++) {
            RemoteServiceCommand command = new RemoteServiceCommand(STR."Request \{i}");
            String result = command.execute();
            System.out.println(result);
        }
    }


    public static class RemoteServiceCommand extends HystrixCommand<String> {

        private final String name;

        public RemoteServiceCommand(String name) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                            .withExecutionTimeoutInMilliseconds(1000)  // Timeout for the command execution
                            .withCircuitBreakerRequestVolumeThreshold(5)  // Minimum number of requests in a rolling window
                            .withCircuitBreakerSleepWindowInMilliseconds(5000)  // Time circuit stays open before retry
                            .withCircuitBreakerErrorThresholdPercentage(50)  // Error percentage to trip the circuit
                            .withMetricsRollingStatisticalWindowInMilliseconds(10000))  // Time window for measuring stats
                    .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter()
                            .withCoreSize(10)  // Core size of the thread pool
                            .withMaxQueueSize(5)  // Max size of the queue for pending tasks
                            .withQueueSizeRejectionThreshold(5)));  // Queue size threshold for rejecting tasks
            this.name = name;
        }

        @Override
        protected String run() throws InterruptedException {
            // Simulate a remote service call
            if (Math.random() > 0.7) {
                System.out.println("Internal server error");
                throw new RuntimeException("Service failure!");
            }else if (Math.random() > 0.5) {
                Thread.sleep(1500);
            }
            return STR."Hello, \{name}!";
        }

        @Override
        protected String getFallback() {
            return STR."Fail fast response. Hello, \{name}!";
        }
    }

}
