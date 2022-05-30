package com.moviescramble.genre.repository;

import com.moviescramble.genre.domain.Genre;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends Neo4jRepository<Genre, Long> {
}
