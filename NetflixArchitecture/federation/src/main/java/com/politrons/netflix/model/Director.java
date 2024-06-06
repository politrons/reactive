package com.politrons.netflix.model;

import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Represents a Director entity with id, name, and surname.
 */
public record Director(String id, String firstName, String lastName) {

    /**
     * The URL of the Director GraphQL endpoint.
     */
    static String url = "http://localhost:8082/graphql";

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
     * Retrieves a Director by its id from the GraphQL endpoint.
     * @param id The id of the Director to retrieve.
     * @return The Director object with the specified id.
     */
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
