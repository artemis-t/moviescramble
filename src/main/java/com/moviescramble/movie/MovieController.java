package com.moviescramble.movie;

import com.moviescramble.movie.domain.Movie;
import com.moviescramble.movie.dto.MovieRatingDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

@RestController
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public Page<Movie> search(@RequestParam(value = "searchText") String searchText,
                              Pageable page) {
        return movieService.search(searchText, page);
    }

    @RequestMapping(value = "/movies/{movieId}/rate", method = RequestMethod.POST)
    public void rate(@Valid @RequestBody MovieRatingDto movieRatingDto,
                     @PathVariable("movieId") long movieId) {
        movieService.rate(movieRatingDto.getRating(), movieId);
    }
}
