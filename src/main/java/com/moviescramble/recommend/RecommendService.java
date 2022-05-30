package com.moviescramble.recommend;

import com.moviescramble.user.ApplicationUserService;
import com.moviescramble.user.domain.ApplicationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendService {

    private final RecommendRepository recommendRepository;
    private final ApplicationUserService applicationUserService;

    @Autowired
    public RecommendService(RecommendRepository recommendRepository, ApplicationUserService applicationUserService) {
        this.recommendRepository = recommendRepository;
        this.applicationUserService = applicationUserService;
    }

    public List<RecommendMovieDto> recommendMoviesWatchedBySimilarUsers() {
        ApplicationUser applicationUser = applicationUserService.getAuthenticatedUser();
        return recommendRepository.recommendMoviesWatchedBySimilarUsers(applicationUser.getId());
    }

    public List<RecommendMovieDto> recommendMoviesWatchedAndLikedBySimilarUsers() {
        ApplicationUser applicationUser = applicationUserService.getAuthenticatedUser();
        return recommendRepository.recommendMoviesWatchedAndLikedBySimilarUsers(applicationUser.getId());
    }

    public List<RecommendMovieDto> recommendMoviesByUsersWithSimilarRatings() {
        ApplicationUser applicationUser = applicationUserService.getAuthenticatedUser();
        return recommendRepository.recommendMoviesLikedByUsersWithSimilarRatings(applicationUser.getId());
    }
}
