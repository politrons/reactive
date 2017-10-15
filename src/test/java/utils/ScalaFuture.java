package utils;

import akka.dispatch.Futures;
import org.junit.Test;
import scala.concurrent.Future;

import java.util.concurrent.CompletableFuture;

import scala.concurrent.Promise;

import static scala.compat.java8.FutureConverters.*;

public class ScalaFuture {


    @Test
    public void test() throws InterruptedException {
        Promise promise = Futures.promise();
        Future scalaFuture = promise.future();
        CompletableFuture cf = toJava(scalaFuture).toCompletableFuture();
        cf.whenComplete((result, t) -> {
            System.out.println(result);
        });
        promise.success("works");
        Thread.sleep(1000);
    }
}
