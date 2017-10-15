package workspace;

import akka.dispatch.Futures;
import scala.concurrent.Future;

public class FutureConnector implements Connector<Future<?>> {

    @Override
    public Future<?> execute() {
        return (Future<String>) Futures.promise().success("Test Future");
    }

}
