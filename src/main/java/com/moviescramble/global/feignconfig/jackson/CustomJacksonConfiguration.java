package com.moviescramble.global.feignconfig.jackson;

import com.fasterxml.jackson.databind.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomJacksonConfiguration {
    @Bean
    public Module myJacksonModule() {
        return new CustomJacksonModule();
    }
}
