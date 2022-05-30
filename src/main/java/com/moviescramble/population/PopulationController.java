package com.moviescramble.population;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class PopulationController {
    private final PopulationService populationService;

    @Autowired
    public PopulationController(PopulationService populationService) {
        this.populationService = populationService;
    }

    @PostMapping(value = "/populate/all")
    public void populateAll() throws IOException {
        populationService.populateAll();
    }

    @PostMapping(value = "/populate/users")
    public void populateUsers() throws IOException {
        populationService.addUsers();
    }

    @PostMapping(value = "/populate/movies")
    public void loadMovies() throws IOException {
        populationService.addMovies();
    }

    @PostMapping(value = "/populate/user-ratings")
    public void loadRatingsAndUsers() throws IOException {
        populationService.addRatings();
        populationService.calculateVotesAndAverage();
    }
}
