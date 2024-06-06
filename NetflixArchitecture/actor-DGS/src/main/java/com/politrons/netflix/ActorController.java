package com.politrons.netflix;

import com.politrons.netflix.model.Actor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ActorController {

    @QueryMapping
    public Actor actorById(@Argument String id) {
        return Actor.getById(id);
    }


}