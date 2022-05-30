package com.moviescramble.nlp.domain;

import org.neo4j.ogm.id.IdStrategy;

public class HasLemmaIdStrategy implements IdStrategy {
    @Override
    public Object generateId(Object entity) {
        HasLemma hasLemma = (HasLemma) entity;
        return generateHasLemmaId(hasLemma.getMovie().getMovieId(), hasLemma.getLemma().getText());
    }

    public static String generateHasLemmaId(Long movieId, String lemma) {
        return "m" + movieId + "_" + lemma;
    }
}
