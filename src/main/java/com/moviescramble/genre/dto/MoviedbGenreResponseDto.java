package com.moviescramble.genre.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class MoviedbGenreResponseDto {
    @JsonProperty("genres")
    @Valid
    private List<MoviedbGenreDto> genreList;

    @Data
    public static class MoviedbGenreDto {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;
    }
}
