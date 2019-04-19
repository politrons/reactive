package com.politrons.quarkus.resource;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.vertx.axle.core.Vertx;
import org.reactivestreams.Publisher;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.parseInt;

/**
 * Quarkus allow add Vertx as part of the ecosystem, allowing us to use all the API of this great library.
 */
@Path("/vertx")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PolitronsVertxResource {

    @Inject
    Vertx vertx;

    /**
     * Using vertx we can use his API and use for instance a delay of a async call.
     */
    @GET
    @Path("/delay/{delay}")
    public CompletionStage<String> getUsersWithDelay(@PathParam("delay") String delay) {
        CompletableFuture<String> future = new CompletableFuture<>();
        vertx.setTimer(parseInt(delay), timerId -> future.complete("Message render after " + delay + " delay and timerId " + timerId));
        return future;
    }

    /**
     * One of the coolest thing that we can do with Publisher/Subscribers is to pass directly the publisher reference
     * to the client as an [EventSource] and then subscribe to it, and start receiving data after an scheduler from the
     * server, instead have to be doing schedule request to the server.
     * <p>
     * To make the whole process work you have to implement the client where through javascript it's able to get the publisher
     * as an [EventSource] and subscribe to it.
     */
    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @Path("/streaming/{name}/{delay}")
    public Publisher<String> streaming(@PathParam("name") String name, @PathParam("delay") String delay) {
        return Observable.interval(parseInt(delay), TimeUnit.MILLISECONDS)
                .map(l -> String.format("Howdy %s! (%s)%n", name, new Date()))
                .map(String::toUpperCase)
                .toFlowable(BackpressureStrategy.DROP);
    }


}