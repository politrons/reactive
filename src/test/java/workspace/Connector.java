package workspace;

import akka.dispatch.Futures;
import scala.concurrent.Future;

public interface Connector<D> {

    <D> D execute();

}
