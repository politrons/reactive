package com.politrons.netflix.model;

import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;

import static java.lang.StringTemplate.STR;

/**
 * Represents an Actor entity with id, name, and surname.
 */
public record Actor(String id, String name, String surname) {

    /**
     * The URL of the Actor GraphQL endpoint.
     */
    static String url = "http://localhost:8081/graphql";

    /**
     * The WebClient used to communicate with the GraphQL endpoint.
     */
    static WebClient client = WebClient.builder()
            .baseUrl(url)
            .build();

    /**
     * The GraphQL client used to execute queries.
     */
    static HttpGraphQlClient graphQlClient = HttpGraphQlClient.builder(client).build();


    /**
     * Retrieves an Actor by its id from the GraphQL endpoint.
     *
     * @param id The id of the Actor to retrieve.
     * @return The Actor object with the specified id.
     */
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
        Actor actor = graphQlClient.document(actorQuery)
                .retrieve("actorById")
                .toEntity(Actor.class)
                .block();
        System.out.println(STR."Actor:\{actor}");
        return actor;
    }
}