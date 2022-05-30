package com.moviescramble.nlp;

import com.moviescramble.movie.domain.Movie;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("nlp")
public class NlpController {

    private final NlpService nlpService;

    @Autowired
    public NlpController(NlpService nlpService) {
        this.nlpService = nlpService;
    }

    @PostMapping("/process-all")
    public void processAll() {
        nlpService.processAll();
    }

    @PostMapping("/create-lemmas-from-all-overviews")
    public void createLemmasFromAllOverviews() {
        nlpService.createLemmasFromAllOverviews();
    }

    @PostMapping("/calculate-tfidf-for-all-overviews")
    public void calculateTfIdForAllOverviews() {
        nlpService.calculateTfIdfForAllOverviews();
    }

    @GetMapping("/movies/{movieId}/recommended")
    public List<Movie> recommendMovies(@PathVariable long movieId, @RequestParam double similarityThreshold) {
        if (similarityThreshold == 0) {
            similarityThreshold = 0.4;
        }
        return nlpService.getRecommendedMovies(movieId, similarityThreshold);
    }

    @GetMapping("/users/me/recommendedMovies")
    public List<Movie> recommendMoviesForUser(
            @RequestParam int ratingThreshold,
            @RequestParam double similarityThreshold) {
        if (ratingThreshold == 0) {
            ratingThreshold = 5;
        }
        if (similarityThreshold == 0) {
            similarityThreshold = 0.4;
        }
        return nlpService.getRecommendedMoviesForUser(ratingThreshold, similarityThreshold);
    }
}
