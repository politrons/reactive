package com.politrons.netflix;

import com.politrons.netflix.model.Actor;
import com.politrons.netflix.model.Director;
import com.politrons.netflix.model.Movie;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

/**
 * The MovieController class handles GraphQL queries and schema mappings related to the Movie entity.
 * This class acts as a Spring controller and defines methods for querying and fetching related data for Movies.
 */
@Controller
public class MovieController {

    /**
     * Handles the GraphQL query for retrieving a Movie by its ID.
     * The @QueryMapping annotation marks this method as a GraphQL query handler.
     * The @Argument annotation indicates that the method parameter should be populated
     * with the value of the specified GraphQL argument.
     *
     * @param id The ID of the Movie to retrieve.
     * @return A Movie object corresponding to the given ID.
     */
    @QueryMapping
    public Movie movieById(@Argument String id) {
        return Movie.getById(id);
    }

    /**
     * Handles the GraphQL schema mapping for retrieving an Actor related to a given Movie.
     * The @SchemaMapping annotation marks this method to handle GraphQL field resolution for the Actor type.
     *
     * @param movie The Movie object for which to fetch the associated Actor.
     * @return An Actor object associated with the given Movie.
     */
    @SchemaMapping
    public Actor actor(Movie movie) {
        return Actor.getById(movie.id());
    }

    /**
     * Handles the GraphQL schema mapping for retrieving a Director related to a given Movie.
     * The @SchemaMapping annotation marks this method to handle GraphQL field resolution for the Director type.
     *
     * @param movie The Movie object for which to fetch the associated Director.
     * @return A Director object associated with the given Movie.
     */
    @SchemaMapping
    public Director director(Movie movie) {
        return Director.getById(movie.director());
    }
}
