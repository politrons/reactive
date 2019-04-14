package com.politrons.quarkus.dao;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class PolitronsQuarkusDAO {

    public CompletableFuture<String> searchUserById(Long userId) {
        return CompletableFuture.supplyAsync(() -> "politrons with id:" + userId);
    }

}
