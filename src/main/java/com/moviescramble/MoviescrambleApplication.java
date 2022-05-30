package com.moviescramble;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MoviescrambleApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoviescrambleApplication.class, args);
    }
}
