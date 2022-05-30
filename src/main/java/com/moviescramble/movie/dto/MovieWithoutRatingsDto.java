package com.moviescramble.movie.dto;

import com.moviescramble.genre.domain.Genre;

import java.util.Date;
import java.util.List;

public interface MovieWithoutRatingsDto {

    Long getMovieId();

    String getTitle();

    String getOriginalTitle();

    String getOriginalLanguage();

    boolean getAdult();

    boolean getVideo();

    String getOverview();

    Date getReleaseDate();

    double getPopularity();

    int getVotes();

    double getVoteAverage();

    String getPosterPath();

    List<Genre> getGenre();
}
