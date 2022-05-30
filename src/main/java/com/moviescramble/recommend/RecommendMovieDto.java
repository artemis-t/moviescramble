package com.moviescramble.recommend;

import com.moviescramble.movie.domain.Movie;
import lombok.Data;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
@Data
public class RecommendMovieDto {
    Movie m;
    double score;
}
