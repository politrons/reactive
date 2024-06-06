package com.politrons.netflix;

import com.politrons.netflix.model.Actor;
import com.politrons.netflix.model.Director;
import com.politrons.netflix.model.Movie;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class MovieController {

    @QueryMapping
    public Movie movieById(@Argument String id) {
        return Movie.getById(id);
    }

    @SchemaMapping
    public Actor actor(Movie movie) {
        return Actor.getById(movie.id());
    }

    @SchemaMapping
    public Director director(Movie movie) {
        return Director.getById(movie.director());
    }
}