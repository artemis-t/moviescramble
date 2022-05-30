package com.moviescramble.movie.domain;

import org.neo4j.ogm.id.IdStrategy;

import java.util.UUID;

public class RatingIdStrategy implements IdStrategy {
    @Override
    public Object generateId(Object entity) {
        Rating rating = (Rating) entity;
        return generateRatingId(rating.getUser().getId(), rating.getMovie().getMovieId());
    }

    public static String generateRatingId(UUID userId, Long movieId) {
        return "u" + userId + "m" + movieId;
    }
}
