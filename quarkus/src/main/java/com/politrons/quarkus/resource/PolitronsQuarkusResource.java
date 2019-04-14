package com.politrons.quarkus.resource;

import com.politrons.quarkus.service.PolitronsQuarkusService;
import io.vertx.axle.core.eventbus.EventBus;
import io.vertx.axle.core.eventbus.Message;

import javax.inject.Inject;
import javax.ws.rs.*;
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
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PolitronsQuarkusResource {

    @Inject
    PolitronsQuarkusService service;

    @Inject
    EventBus bus;

    @GET
    public String first_resource() {
        return "Version 1.0 of Quarkus in Politrons system";
    }

    @GET
    @Path("/user/sync/{userId}")
    public String getUsersSyncResource(@PathParam("userId") String id) {
        return service.getUser(Long.valueOf(id));
    }

    /**
     * Quarkus allow return directly the CompletionStage, making the service [Serverless] since the transport it's agnostic.
     */
    @GET
    @Path("/user/async/{userId}")
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
    public CompletionStage<String> getUsersBusResource(@PathParam("userId") String id) {
        return bus.<String>send("getUserById", Long.valueOf(id))
                .thenApply(Message::body);
    }
}