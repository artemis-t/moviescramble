package com.moviescramble.user.dto;

import com.moviescramble.country.Country;
import com.moviescramble.user.domain.RoleName;
import com.moviescramble.user.domain.Sex;

import java.util.UUID;

public interface UserListingDto {
    UUID getId();

    String getEmail();

    int getBirthYear();

    Sex getSex();

    Country getCountry();

    RoleName getRole();
}
