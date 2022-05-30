package com.moviescramble.movie.dto.moviedb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MoviedbSearchResponseDto {

    @JsonProperty("page")
    private int page;

    @JsonProperty("total_results")
    private int totalResults;

    @JsonProperty("total_pages")
    private int totalPages;

    private List<MoviedbMovieDto> results;

    @Data
    public static class MoviedbMovieDto {

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

        @JsonProperty("genre_ids")
        private List<Long> genres;
    }
}