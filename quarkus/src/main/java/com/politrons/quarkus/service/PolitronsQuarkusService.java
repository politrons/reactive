package com.politrons.quarkus.service;


import com.politrons.quarkus.dao.PolitronsQuarkusDAO;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PolitronsQuarkusService {

    @Inject
    PolitronsQuarkusDAO dao;

    public String getUser(Long id) {
        return dao.searchUserById(id);
    }

}
