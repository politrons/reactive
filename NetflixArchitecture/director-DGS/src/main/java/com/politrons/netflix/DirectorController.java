package com.politrons.netflix;

import com.politrons.netflix.model.Director;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * The DirectorController class handles GraphQL queries related to the Director entity.
 * This class acts as a Spring controller and defines methods for querying Director data.
 */
@Controller
public class DirectorController {

    /**
     * Handles the GraphQL query for retrieving a Director by their ID.
     * The @QueryMapping annotation marks this method as a GraphQL query handler.
     * The @Argument annotation indicates that the method parameter should be populated
     * with the value of the specified GraphQL argument.
     *
     * @param id The ID of the Director to retrieve.
     * @return A Director object corresponding to the given ID.
     */
    @QueryMapping
    public Director directorById(@Argument String id) {
        return Director.getById(id);
    }


}