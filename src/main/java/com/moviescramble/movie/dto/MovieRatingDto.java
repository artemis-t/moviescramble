package com.moviescramble.movie.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class MovieRatingDto {
    @Min(1)
    @Max(10)
    private int rating;
}
