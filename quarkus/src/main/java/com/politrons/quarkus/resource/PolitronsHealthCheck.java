package com.politrons.quarkus.resource;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;

/**
 * Health check implementation in Quarkus it's so simple enough as add the annotation
 * @Health and @ApplicationScoped to be injected and implement [HealthCheck] which it
 * will force you to implement [call] method which it will return a [HealthCheckResponse]
 */
@Health
@ApplicationScoped
public class PolitronsHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.named("Politrons health check").up()
                .withData("Oracle database", "running")
                .withData("Cassandra database", "running")
                .build();
    }
}