package workspace;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

public class FutureDao extends DaoConnector {

    public void searchString() throws Exception {
        Future<String> future = executeString();
        Object result = Await.result(future, Duration.create(10, TimeUnit.SECONDS));
        System.out.println("Future value:" + result);
    }

    public void searchInt() throws Exception {
        Future<Integer> future = executeInt();
        Object result = Await.result(future, Duration.create(10, TimeUnit.SECONDS));
        System.out.println("Future value:" + result);
    }

}
