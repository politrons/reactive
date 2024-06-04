package com.politrons.netflix;

import java.util.Arrays;
import java.util.List;

public record Director(String id, String firstName, String lastName) {

    private static List<Director> directors = Arrays.asList(
            new Director("author-1", "Joshua", "Bloch"),
            new Director("2", "Douglas", "Adams"),
            new Director("3", "Andy and Larry", "Wachowski")
    );

    public static Director getById(String id) {
        return directors.stream()
                .filter(author -> author.id().equals(id))
                .findFirst()
                .orElse(null);
    }
}
