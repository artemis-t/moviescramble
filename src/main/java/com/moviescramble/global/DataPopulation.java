package com.moviescramble.global;

import com.moviescramble.country.CountryService;
import com.moviescramble.genre.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DataPopulation {

    private final GenreService genreService;

    private final CountryService countryService;

    @Autowired
    public DataPopulation(GenreService genreService, CountryService countryService) {
        this.genreService = genreService;
        this.countryService = countryService;
    }

    @PostConstruct
    public void init() throws Exception {
        genreService.populateGenreTable();
        countryService.populateCountries();
    }
}
