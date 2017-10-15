package workspace;

import akka.dispatch.Futures;
import scala.concurrent.Future;

public class FutureConnector implements Connector<Future<?>> {

    @Override
    public Future<String> executeString() {
        return (Future<String>) Futures.promise().success("Test Future");
    }

    @Override
    public Future<Integer> executeInt() {
        return (Future<Integer>) Futures.promise().success(1981);
    }

}
