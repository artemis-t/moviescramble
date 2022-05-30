package com.moviescramble.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moviescramble.global.customannotations.PasswordMatches;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@PasswordMatches
public class UserPasswordResetDto {

    @NotBlank
    @JsonProperty(value = "password")
    private String currentPassword;

    @NotBlank
    @JsonProperty(value = "new_password")
    private String newPassword;

    @NotBlank
    @JsonProperty(value = "match_password")
    private String passwordMatch;

}
