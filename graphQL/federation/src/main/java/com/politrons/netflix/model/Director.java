package com.politrons.netflix.model;

import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;

public record Director(String id, String firstName, String lastName) {

    static String url = "http://localhost:8082/graphql";

    static WebClient client = WebClient.builder()
            .baseUrl(url)
            .build();
    static HttpGraphQlClient graphQlClient = HttpGraphQlClient.builder(client).build();

    public static Director getById(String id) {
        String actorQuery = STR."""
            query {
               directorById(id:"\{id}") {
                          id
                          firstName
                          lastName
                }
            }
            """;
        Director director = graphQlClient.document(actorQuery).retrieve("directorById").toEntity(Director.class).block();
        System.out.println(STR."Director:\{director}");
        return director;
    }
}
