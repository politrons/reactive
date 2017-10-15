package workspace;

import org.junit.Test;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureDao extends DaoConnector {

    public void search() throws Exception {
        CompletableFuture<String> future = execute();
        future.whenComplete((r, t) -> {
            System.out.println("Completable future value:" + r);
        });
        Thread.sleep(1000);
    }


    @Override
    public Class getReturnTypeClass() {
        return CompletableFuture.class;
    }
}
