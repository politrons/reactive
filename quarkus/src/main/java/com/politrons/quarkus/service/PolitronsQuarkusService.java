package com.politrons.quarkus.service;


import com.politrons.quarkus.dao.PolitronsQuarkusDAO;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * To use CDI and use bean-discovery, we have to use the @ApplicationScoped instead of @Singleton.
 * The differences are that Singleton cannot be injected in compilation time of the Jar.
 */
@ApplicationScoped
public class PolitronsQuarkusService {

    @Inject
    PolitronsQuarkusDAO dao;

    public String getUser(Long id) {
        return dao.searchUserById(id);
    }

}
