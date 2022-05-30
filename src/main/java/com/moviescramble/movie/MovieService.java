package com.moviescramble.movie;


import com.moviescramble.genre.domain.Genre;
import com.moviescramble.genre.repository.GenreRepository;
import com.moviescramble.global.customexceptions.MoviescrambleException;
import com.moviescramble.movie.client.MoviedbClient;
import com.moviescramble.movie.domain.Movie;
import com.moviescramble.movie.domain.Rating;
import com.moviescramble.movie.dto.moviedb.MoviedbSearchResponseDto;
import com.moviescramble.movie.repository.MovieRepository;
import com.moviescramble.user.ApplicationUserService;
import com.moviescramble.user.domain.ApplicationUser;
import com.moviescramble.user.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MoviedbClient movieClient;
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final ApplicationUserService applicationUserService;

    @Autowired
    private MovieService self;

    @Autowired
    public MovieService(MoviedbClient movieClient, MovieRepository movieRepository, GenreRepository genreRepository, ApplicationUserRepository applicationUserRepository, ApplicationUserService applicationUserService) {
        this.movieClient = movieClient;
        this.movieRepository = movieRepository;
        this.genreRepository = genreRepository;
        this.applicationUserRepository = applicationUserRepository;
        this.applicationUserService = applicationUserService;
    }

    public Page<Movie> search(String searchText, Pageable page) {
        Page<MoviedbSearchResponseDto.MoviedbMovieDto> moviedbMovieDtos = movieClient.search(searchText, page);
        List<Movie> movieList = new ArrayList<>();
        for (MoviedbSearchResponseDto.MoviedbMovieDto movie : moviedbMovieDtos.getContent()) {
            Iterable<Genre> movieGenres = genreRepository.findAllById(movie.getGenres());
            List<Genre> genreList = new ArrayList<>();
            movieGenres.forEach(genreList::add);
            movieList.add(
                    Movie.builder()
                            .movieId(movie.getId())
                            .title(movie.getTitle()).originalTitle(movie.getTitle())
                            .originalLanguage(movie.getOriginalLanguage()).adult(movie.isAdult())
                            .overview(movie.getOverview()).posterPath(movie.getPosterPath())
                            .popularity(movie.getPopularity())
                            .video(movie.isVideo())
                            .genre(genreList)
                            .build());
        }
        return new PageImpl<>(movieList, page, moviedbMovieDtos.getTotalElements());
    }

    @Transactional
    public void rate(int stars, long movieId) {
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        if (!optionalMovie.isPresent()) {
            throw new MoviescrambleException("Movie was not found", HttpStatus.NOT_FOUND);
        }

        ApplicationUser applicationUser = applicationUserService.getAuthenticatedUser();
        Movie movie = optionalMovie.get();
        Rating rating = movie.getExistingRating(applicationUser);
        if (rating == null) {
            rating = Rating.builder()
                    .movie(movie)
                    .stars(stars)
                    .user(applicationUser)
                    .build();
            movie.setVotes(movie.getVotes() + 1);
        } else {
            rating.setStars(stars);
        }

        rating.setRatingDate(new Date());
        movie.addRating(rating);
        applicationUser.addRating(rating);
        applicationUserRepository.save(applicationUser);
        self.calculateAverageAndVotes(movieId);
    }

    @Async
    @Transactional
    public void calculateAverageAndVotes(long movieId) {
        movieRepository.saveVotesAndAverage(movieId);
    }
}