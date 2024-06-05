package com.politrons.netflix;

import java.util.Arrays;
import java.util.List;

public record Movie(String id, String name, int duration, String director) {

    private static List<Movie> movies = Arrays.asList(
            new Movie("1", "Effective Java", 416, "author-1"),
            new Movie("2", "Hitchhiker's Guide to the Galaxy", 117, "author-2"),
            new Movie("3", "The Matrix", 121, "Wachowski")
    );

    public static Movie getById(String id) {
        return movies.stream()
                .filter(movie -> movie.id().equals(id))
                .findFirst()
                .orElse(null);
    }
}