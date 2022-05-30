package com.moviescramble.movie.client;

import com.moviescramble.genre.dto.MoviedbGenreResponseDto;
import com.moviescramble.movie.dto.moviedb.MoviedbSearchResponseDto;
import feign.Headers;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "moviedbClient", url = "https://${moviedb.url}/${moviedb.version}")
public interface MoviedbClient {

    @RequestMapping(method = RequestMethod.GET, path = "/search/movie?api_key=${moviedb.key}&language=${moviedb.language}&query={searchText}&include_adult=${moviedb.include_adult}&page={page}")
    Page<MoviedbSearchResponseDto.MoviedbMovieDto> search(@RequestParam("searchText") String searchText, @RequestBody Pageable page);

    @RequestMapping(method = RequestMethod.GET, path = "/genre/movie/list?api_key=${moviedb.key}")
    MoviedbGenreResponseDto getMovieGenreList();

    @RequestMapping(method = RequestMethod.GET, path = "/genre/tv/list?api_key=${moviedb.key}")
    MoviedbGenreResponseDto getTvGenreList();

    @Headers({
            "Content-Type: " + MediaType.APPLICATION_JSON_UTF8_VALUE
    })
    @RequestMapping(method = RequestMethod.GET, path = "/movie/{movieId}?api_key=${moviedb.key}&language=${moviedb.language}")
    Response getMovieById(@RequestParam("movieId") long movieId);
}
