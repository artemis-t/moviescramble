package com.moviescramble.nlp;

import com.moviescramble.nlp.domain.Lemma;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface LemmaRepository extends Neo4jRepository<Lemma, Integer> {

    @Query("MATCH (l:lemma) where l.text IN {0} RETURN l ")
    Set<Lemma> findAllByLemmaIn(List<String> lemmaList);
}
