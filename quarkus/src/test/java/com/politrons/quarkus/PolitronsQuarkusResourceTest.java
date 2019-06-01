package com.politrons.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class PolitronsQuarkusResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/info")
          .then()
             .statusCode(200)
             .body(is("Version 1.0 of Quarkus in Politrons system"));
    }

}