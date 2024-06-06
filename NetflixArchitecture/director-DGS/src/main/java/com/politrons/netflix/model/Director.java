package com.politrons.netflix.model;

import java.util.Arrays;
import java.util.List;

public record Director(String id, String firstName, String lastName) {

    private static List<Director> directors = Arrays.asList(
            new Director("1", "Ridley", "Scott"),
            new Director("2", "Steven", "Spielberg"),
            new Director("3", "Andy and Larry", "Wachowski")
    );

    public static Director getById(String id) {
        return directors.stream()
                .filter(author -> author.id().equals(id))
                .findFirst()
                .orElse(null);
    }
}
