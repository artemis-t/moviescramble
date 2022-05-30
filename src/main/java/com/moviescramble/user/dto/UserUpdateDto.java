package com.moviescramble.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moviescramble.global.customannotations.UserDetails;
import com.moviescramble.user.domain.Sex;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserUpdateDto {

    @NotBlank
    private String email;

    @JsonProperty(value = "birth_year")
    private int birthYear;

    @JsonProperty(value = "sex")
    @NotNull
    private Sex sex;

    @JsonProperty(value = "country")
    @NotBlank
    @Size(min = 2, max = 2)
    private String country;
}
