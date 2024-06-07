package com.politrons.netflix;

import com.politrons.netflix.model.Actor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * The ActorController class handles GraphQL queries related to the Actor entity.
 * This class acts as a Spring controller and defines methods for querying Actor data.
 */
@Controller
public class ActorController {

    /**
     * Handles the GraphQL query for retrieving an Actor by their ID.
     * The @QueryMapping annotation marks this method as a GraphQL query handler.
     * The @Argument annotation indicates that the method parameter should be populated
     * with the value of the specified GraphQL argument.
     *
     * @param id The ID of the Actor to retrieve.
     * @return An Actor object corresponding to the given ID.
     */
    @QueryMapping
    public Actor actorById(@Argument String id) {
        return Actor.getById(id);
    }


}