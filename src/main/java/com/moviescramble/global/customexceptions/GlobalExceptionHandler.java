package com.moviescramble.global.customexceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MoviescrambleException.class})
    public void handleMoviescrambleException(
            MoviescrambleException e, HttpServletResponse response) throws IOException {
        response.sendError(e.getHttpStatus().value(), e.getMessage());
    }

}
