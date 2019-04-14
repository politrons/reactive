package com.politrons.quarkus.dao;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PolitronsQuarkusDAO {

    public String searchUserById(Long userId){
        return "politrons with id:" + userId;
    }

}
