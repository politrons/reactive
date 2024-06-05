package com.politrons.netflix;

import com.politrons.netflix.model.Director;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class DirectorController {

    @QueryMapping
    public Director directorById(@Argument String id) {
        return Director.getById(id);
    }


}