package workspace;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureDao extends DaoConnector {

    public void searchString() throws Exception {
        CompletableFuture<String> future = executeString();
        future.whenComplete((r, t) -> System.out.println("Completable future value:" + r));
        Thread.sleep(1000);
    }

    public void searchInt() throws Exception {
        CompletableFuture<Integer> future = executeInt();
        future.whenComplete((r, t) -> System.out.println("Completable future value:" + r));
        Thread.sleep(1000);
    }

    @Override
    public Class getReturnTypeClass() {
        return CompletableFuture.class;
    }
}
