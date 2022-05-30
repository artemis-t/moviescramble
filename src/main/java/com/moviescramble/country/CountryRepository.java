package com.moviescramble.country;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends Neo4jRepository<Country, String> {

    Optional<Country> findByIsoCode(String isoCode);
}
