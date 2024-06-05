package com.politrons.netflix.model;

import java.util.Arrays;
import java.util.List;

public record Movie(String id, String name, int duration, String director) {

    private static List<Movie> movies = Arrays.asList(
            new Movie("1", "The Martian", 214, "1"),
            new Movie("2", "Minority Report", 117, "2"),
            new Movie("3", "The Matrix", 121, "3")
    );

    public static Movie getById(String id) {
        return movies.stream()
                .filter(movie -> movie.id().equals(id))
                .findFirst()
                .orElse(null);
    }
}