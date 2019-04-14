package com.politrons.quarkus.resource;

import com.politrons.quarkus.service.PolitronsQuarkusService;
import io.vertx.axle.core.eventbus.EventBus;
import io.vertx.axle.core.eventbus.Message;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletionStage;

/**
 * Quarkus use JAX-RS standard for REST API, all annotation included the @Inject for DI
 * are Java standard.
 * In case of JAX-RS is JBoss implementation.
 *
 * With Quarkus we can return a CompletionStage making the whole Request/Response Asynchronous.
 */
@Path("/info")
public class PolitronsQuarkusResource {

    @Inject
    PolitronsQuarkusService service;

    @Inject
    EventBus bus;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String first_resource() {
        return "Version 1.0 of Quarkus in Politrons system";
    }

    @GET
    @Path("/user/sync/{userId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getUsersSyncResource(@PathParam("userId") String id) {
        return service.getUser(Long.valueOf(id));
    }

    /**
     * Quarkus allow return directly the CompletionStage, making the service [Serverless] since the transport it's agnostic.
     */
    @GET
    @Path("/user/async/{userId}")
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> getUsersAsyncResource(@PathParam("userId") String id) {
        return service.getUserAsync(Long.valueOf(id));
    }

    /**
     * Quarkus add some features from Vert.x, like the Event Bus. Which allow the usual message driven patterns.
     *
     * * send a message to a specific address - one single consumer receives the message.
     * * publish a message to a specific address - all consumers receive the messages.
     * * send a message and expect reply
     */
    @GET
    @Path("/user/bus/{userId}")
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> getUsersBusResource(@PathParam("userId") String id) {
        return bus.<String>send("getUserById", Long.valueOf(id))
                .thenApply(Message::body);
    }
}