package com.moviescramble.global.securityconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtAuthenticationFilterConfig {
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsImpl userDetailsImpl) {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsImpl);
    }
}
