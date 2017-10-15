package workspace;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureConnector implements Connector<CompletableFuture<?>> {

    @Override
    public CompletableFuture<?> execute() {
        return CompletableFuture.supplyAsync(() -> "Test CompletableFuture");
    }

}
