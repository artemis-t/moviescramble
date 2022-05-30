package com.moviescramble.genre.service;

import com.moviescramble.genre.domain.Genre;
import com.moviescramble.genre.dto.MoviedbGenreResponseDto;
import com.moviescramble.genre.repository.GenreRepository;
import com.moviescramble.movie.client.MoviedbClient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GenreService {

    private final MoviedbClient moviedbClient;

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(MoviedbClient moviedbClient, GenreRepository genreRepository) {
        this.moviedbClient = moviedbClient;
        this.genreRepository = genreRepository;
    }

    public void populateGenreTable() throws Exception {
        log.info("Updating Movie genres from TheMovieDB...");
        MoviedbGenreResponseDto movieGenreDto = moviedbClient.getMovieGenreList();
        saveGenres(movieGenreDto);

        log.info("Updating TV genres from TheMovieDB...");
        MoviedbGenreResponseDto tvGenreDto = moviedbClient.getTvGenreList();
        saveGenres(tvGenreDto);
    }

    private void saveGenres(MoviedbGenreResponseDto genreDto) throws Exception {
        if (genreDto == null) {
            throw new Exception("empty genre list");
        }

        for (MoviedbGenreResponseDto.MoviedbGenreDto moviedbGenre : genreDto.getGenreList()) {
            if (moviedbGenre.getName().equals("Science Fiction")) {
                moviedbGenre.setName("Sci-Fi");
            }

            if (moviedbGenre.getName().equals("Kids")) {
                moviedbGenre.setName("Children");
            }

            if (moviedbGenre.getName().equals("Music")) {
                moviedbGenre.setName("Musical");
            }

            genreRepository.save(
                    Genre.builder()
                            .genreId(moviedbGenre.getId())
                            .name(moviedbGenre.getName())
                            .build());
        }
    }
}
