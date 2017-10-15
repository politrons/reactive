package workspace;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureConnector implements Connector<CompletableFuture<?>> {

    @Override
    public CompletableFuture<String> executeString() {
        return CompletableFuture.supplyAsync(() -> "Test CompletableFuture");
    }

    @Override
    public CompletableFuture<Integer> executeInt() {
        return CompletableFuture.supplyAsync(() -> 1891);
    }
}
