package com.moviescramble.global.customexceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MoviescrambleException extends RuntimeException {
    private HttpStatus httpStatus;

    public MoviescrambleException(String exceptionMessage, HttpStatus httpStatus) {
        super(exceptionMessage);
        this.httpStatus = httpStatus;
    }
}
