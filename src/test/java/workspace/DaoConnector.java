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

    public <D> D executeString() {
        return (D) connector.executeString();
    }

    public <D> D executeInt() {
        return (D) connector.executeInt();
    }

    public Class getReturnTypeClass() {
        return Future.class;
    }

}
