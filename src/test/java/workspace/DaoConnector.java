package workspace;

import scala.concurrent.Future;

import java.util.concurrent.CompletableFuture;

public abstract class DaoConnector {

    Connector connector;

    public DaoConnector() {
        if (getReturnTypeClass().isAssignableFrom(Future.class)) {
            connector = new FutureConnector();
        } else if (getReturnTypeClass().isAssignableFrom(CompletableFuture.class)) {
            connector = new CompletableFutureConnector();
        }
    }

    public <D> D execute() {
        return (D) connector.execute();
    }

    public Class getReturnTypeClass() {
        return Future.class;
    }

}
