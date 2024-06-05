package com.politrons.netflix;

import java.util.Arrays;
import java.util.List;

public record Actor(String id, String name, String surname) {

    private static List<Actor> actors = Arrays.asList(
            new Actor("1", "Matt", "Damon"),
            new Actor("2", "Tom",  "Cruise"),
            new Actor("3", "Keanu", "Revues")
    );

    public static Actor getById(String id) {
        return actors.stream()
                .filter(actor -> actor.id().equals(id))
                .findFirst()
                .orElse(null);
    }
}