package com.moviescramble.movie.dto.moviedb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.moviescramble.genre.dto.MoviedbGenreResponseDto;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MoviedbGetMovieDto {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("title")
    private String title;

    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("adult")
    private boolean adult;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("popularity")
    private double popularity;

    @JsonProperty("video")
    private boolean video;

    @JsonProperty("imdb_id")
    private String imdbId;


    @JsonProperty("status")
    private String status;

    @JsonProperty("genres")
    private List<MoviedbGenreResponseDto.MoviedbGenreDto> genres;
}
