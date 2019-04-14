package com.politrons.quarkus.service;


import com.politrons.quarkus.dao.PolitronsQuarkusDAO;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

/**
 * To use CDI and use bean-discovery, we have to use the @ApplicationScoped instead of @Singleton.
 * The differences are that Singleton cannot be injected in compilation time of the Jar.
 */
@ApplicationScoped
public class PolitronsQuarkusService {

    @Inject
    PolitronsQuarkusDAO dao;

    public String getUser(Long id) {
        try {
            return dao.searchUserById(id).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return "User not found";
    }

    public CompletionStage<String> getUserAsync(Long id) {
        return dao.searchUserById(id);
    }

}
