package com.moviescramble.recommend;

import com.moviescramble.movie.domain.Movie;
import java.util.List;
import java.util.UUID;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendRepository extends Neo4jRepository<Movie, Long> {

    @Query("MATCH (u1:user {id:{0}})-[:RATED]->(m1:movie) " +
            "WITH count(m1) as countm " +
            "MATCH (u1:user {id:{0}}) " +
            "MATCH (m1)<-[r:RATED]-(u2:user) " +
            "WHERE NOT u2=u1 " +
            "WITH u2, countm, tofloat(count(r))/countm as sim " +
            "WHERE sim>0.5 " +
            "WITH count(u2) as countu, countm " +
            "MATCH (u1:user {id:{0}})-[:RATED]->(m1:movie) " +
            "MATCH (m1)<-[r:RATED]-(u2:user) " +
            "WHERE NOT u2=u1 " +
            "WITH u1, u2,countu, tofloat(count(r))/countm as sim " +
            "WHERE sim> 0.5 " +
            "MATCH (m:movie)<-[r:RATED]-(u2) " +
            "WHERE NOT (m)<-[:RATED]-(u1) " +
            "RETURN DISTINCT m, tofloat(count(r))/countu as score ORDER BY score DESC")
    List<RecommendMovieDto> recommendMoviesWatchedBySimilarUsers(UUID userId);

    @Query("MATCH (m1:movie)<-[:RATED]-(u1:user{id:{0}}) " +
            "WITH count(m1) as countm " +
            "MATCH (u2:user)-[r2:RATED]->(m1:movie)<-[r1:RATED]-(u1:user{id:{0}}) " +
            "WHERE (NOT u2=u1) AND (abs(r2.stars - r1.stars) <= 1) " +
            "WITH u1, u2, tofloat(count(DISTINCT m1))/countm as sim " +
            "WHERE sim>0.5 " +
            "MATCH (m:movie)<-[r:RATED]-(u2) " +
            "WHERE (NOT (m)<-[:RATED]-(u1)) " +
            "RETURN DISTINCT m,tofloat(sum(r.stars)) as score ORDER BY score DESC ")
    List<RecommendMovieDto> recommendMoviesWatchedAndLikedBySimilarUsers(UUID userId);

    @Query("MATCH (m1:movie)<-[:RATED]-(u1:user {id:{0}}) " +
            "WITH count(m1) as countm " +
            "MATCH (u2:user)-[r2:RATED]->(m1:movie)<-[r1:RATED]-(u1:user {id:{0}}) " +
            "WHERE (NOT u2=u1) AND (abs(r2.stars - r1.stars) <= 1) " +
            "WITH u1, u2, tofloat(count(DISTINCT m1))/countm as sim " +
            "WHERE sim>0.5 " +
            "MATCH (m:movie)<-[r:RATED]-(u2) " +
            "WHERE (NOT (m)<-[:RATED]-(u1)) " +
            "WITH DISTINCT m, count(r) as n_u, tofloat(sum(r.stars)) as sum_r " +
            "WHERE n_u > 1 " +
            "RETURN m, sum_r/n_u as score ORDER BY score DESC")
    List<RecommendMovieDto> recommendMoviesLikedByUsersWithSimilarRatings(UUID userId);


    @Query("MATCH (user:user {id:{0}}) "
            + "MATCH (watched:movie) "
            + "WHERE ((user)-[:RATED]->(watched)) "
            + "WITH collect(watched.movieId) as watched_set, user "

            + "MATCH (user)-[rate:RATED]->(liked:movie) "
            + "WHERE rate.stars > {1} "
            + "MATCH (liked)-[r:HAS_LEMMA]->(l:lemma) "
            + "WHERE r.tfidf >= {2} "
            + "WITH collect(l.text) as lem, watched_set "

            + "MATCH(m:movie)-[r]->(n:lemma) where r.tfidf>={2} and n.text in lem and not m.movieId in watched_set "
            + "RETURN distinct m, sum(r.tfidf) as score "
            + "ORDER BY score DESC ")
    List<RecommendMovieDto> getRecommendedMoviesForUser(UUID userId, int ratingThreshold, double similarityThreshold);


    @Query("MATCH (m:movie)-[r:HAS_LEMMA]->(l:lemma) "
            + "WHERE m.movieId={0} and r.tfidf >= {1} "
            + "WITH collect(l.text) as lem "

            + "MATCH(m)-[r]->(n:lemma) where m.movieId<>{0} and r.tfidf>={1} and n.text in lem "
            + "RETURN distinct m, sum(r.tfidf) as score "
            + "ORDER BY score DESC")
    // TODO add genres?
    List<RecommendMovieDto> getSimilarMovies(long movieId, double similarityThreshold);
}
