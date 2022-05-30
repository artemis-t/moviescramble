package com.moviescramble.movie.repository;

import com.moviescramble.movie.domain.Movie;
import com.moviescramble.movie.domain.Rating;
import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface MovieRepository extends Neo4jRepository<Movie, Long> {

    @Query("MATCH (u:user),(m:movie) WHERE u.email={1} AND m.movieId={0} " +
            "CREATE (u)-[r:RATED]->(m) " +
            "SET r.stars={2}, r.ratingDate={3}, r.id={4}")
    void saveRating(long movieId, String email, int stars, Date date, String ratingId);

    @Query("MATCH (u:user)-[r:RATED]->(m:movie) WHERE u.email={1} AND m.movieId={0} RETURN r")
    Rating findRatingByMovieIdAndUserEmail(long movieId, String email);

    @Query("MATCH (u:user)-[r:RATED]->(m:movie) where m.movieId={0} " +
            "with avg(r.stars) as averageRating, " +
            "count(r) as votes, m  set m.votes=votes, m.voteAverage=averageRating")
    void saveVotesAndAverage(long movieId);

    Page<Movie> findMovieByProcessed(boolean processed, Pageable pageable);

    @Query(value = "MATCH (m:movie)-[r:HAS_LEMMA]->() where m.processed={0} and not exists(r.tfidf) return distinct m",
            countQuery = "MATCH (m:movie)-[r:HAS_LEMMA]->() where m.processed={0} and not exists(r.tfidf) return count(distinct m)")
    Page<Movie> findMovieWithoutTfidf(boolean processed, Pageable pageable);

    Long countAllByOverviewExists();

    @Query("MATCH (m:movie)-[r:HAS_LEMMA]->(l:lemma) where l.text={0} return count(m)")
    Long countMoviesWithLemma(String lemma);
}
