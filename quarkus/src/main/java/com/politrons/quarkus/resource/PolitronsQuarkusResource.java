package com.politrons.quarkus.resource;

import com.politrons.quarkus.service.PolitronsQuarkusService;

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

    @GET
    @Path("/user/async/{userId}")
    @Produces(MediaType.TEXT_PLAIN)
    public CompletionStage<String> getUsersAsyncResource(@PathParam("userId") String id) {
        return service.getUserAsync(Long.valueOf(id));
    }


}