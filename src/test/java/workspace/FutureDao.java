package workspace;

import org.junit.Test;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class FutureDao extends DaoConnector {

    public void search() throws Exception {
        Future<String> future = execute();
        Object result = Await.result(future, Duration.create(10, TimeUnit.SECONDS));
        System.out.println("Future value:" + result);
    }

}
