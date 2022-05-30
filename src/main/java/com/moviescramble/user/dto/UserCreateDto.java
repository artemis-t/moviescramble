package com.moviescramble.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moviescramble.user.domain.Sex;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {
    @NotBlank
    private String email;

    @JsonProperty(value = "password")
    @NotBlank
    private String password;

    @JsonProperty(value = "password_match")
    @NotBlank
    private String matchingPassword;

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
