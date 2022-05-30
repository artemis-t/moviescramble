package com.moviescramble.recommend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RecommendController {

    private final RecommendService recommendService;

    @Autowired
    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    @RequestMapping(value = "/recommend/similar-watched-movies")
    public List<RecommendMovieDto> recommendMoviesWatchedBySimilarUsers() {
        return recommendService.recommendMoviesWatchedBySimilarUsers();
    }

    @RequestMapping(value = "/recommend/liked-similar-movies")
    public List<RecommendMovieDto> recommendMoviesLikedBySimilarUsers() {
        return recommendService.recommendMoviesWatchedAndLikedBySimilarUsers();
    }

    @RequestMapping(value = "/recommend/similar-movie-ratings")
    public List<RecommendMovieDto> recommendMoviesByUsersWithSimilarRatings() {
        return recommendService.recommendMoviesByUsersWithSimilarRatings();
    }

    @GetMapping(value = "/recommend/similar-movies")
    public List<RecommendMovieDto> recommendSimilarMovies() {
        throw new UnsupportedOperationException("TODO");
    }
}
