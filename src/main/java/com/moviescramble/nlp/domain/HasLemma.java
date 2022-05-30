package com.moviescramble.nlp.domain;

import com.moviescramble.movie.domain.Movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "HAS_LEMMA")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HasLemma {
    @Id
    @GeneratedValue(strategy = HasLemmaIdStrategy.class)
    private String id;

    private Double tfidf;

    @StartNode
    private Movie movie;

    @EndNode
    private Lemma lemma;
}
