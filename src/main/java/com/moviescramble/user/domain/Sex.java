package com.moviescramble.user.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Sex {
    @JsonProperty("male") MALE,
    @JsonProperty("female") FEMALE,
    @JsonProperty("non-disclosed") NON_DISCLOSED
}
