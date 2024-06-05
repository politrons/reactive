package com.politrons.netflix;

import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;

public record Actor(String id, String name, String surname) {

    static String url = "http://localhost:8081/graphql";

    static WebClient client = WebClient.builder()
            .baseUrl(url)
            .build();
    static HttpGraphQlClient graphQlClient = HttpGraphQlClient.builder(client).build();

    static String actorQuery = """
            query {
               actorById(id:"1") {
                          id
                          name
                          surname
                }
            }
            """;

    public static Actor getById(String id) {
        Actor actor = graphQlClient.document(actorQuery).retrieve("actorById").toEntity(Actor.class).block();
        System.out.println("Actor:" + actor);
        return actor;
    }
}