package com.politrons.netflix.model;

import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;

import static java.lang.StringTemplate.STR;

public record Actor(String id, String name, String surname) {

    static String url = "http://localhost:8081/graphql";

    static WebClient client = WebClient.builder()
            .baseUrl(url)
            .build();
    static HttpGraphQlClient graphQlClient = HttpGraphQlClient.builder(client).build();


    public static Actor getById(String id) {
        String actorQuery = STR."""
            query {
               actorById(id:"\{id}") {
                          id
                          name
                          surname
                }
            }
            """;
        Actor actor = graphQlClient.document(actorQuery).retrieve("actorById").toEntity(Actor.class).block();
        System.out.println(STR."Actor:\{actor}");
        return actor;
    }
}