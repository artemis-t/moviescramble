package com.moviescramble.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter

public class UserLoginDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
