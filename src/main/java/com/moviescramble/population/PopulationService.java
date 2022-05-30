package com.moviescramble.population;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviescramble.genre.domain.Genre;
import com.moviescramble.movie.client.MoviedbClient;
import com.moviescramble.movie.domain.Movie;
import com.moviescramble.movie.domain.Rating;
import com.moviescramble.movie.domain.RatingIdStrategy;
import com.moviescramble.movie.dto.moviedb.MoviedbGetMovieDto;
import com.moviescramble.movie.repository.MovieRepository;
import com.moviescramble.user.ApplicationUserService;
import com.moviescramble.user.domain.ApplicationUser;
import com.moviescramble.user.domain.Sex;
import com.moviescramble.user.dto.UserCreateDto;
import com.moviescramble.user.dto.UserWithoutRatingsDto;
import com.moviescramble.user.repository.ApplicationUserRepository;
import com.moviescramble.utils.DateUtils;
import feign.Response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.moviescramble.utils.StreamUtils.parseCSV;

@Slf4j
@Service
public class PopulationService {

    @Value("${movielens.links.file:links.csv}")
    private String linksFile;

    @Value("${movielens.ratings.file:ratings-withTmdbId.csv}")
    private String ratingsFile;

    private static final String HEADER_LIMIT_REMAINING = "x-ratelimit-remaining";
    private static final String HEADER_LIMIT_RESET = "x-ratelimit-reset";
    private static final String CSV_DELIMITER = ",";

    private final MovieRepository movieRepository;
    private final ApplicationUserService applicationUserService;
    private final ApplicationUserRepository applicationUserRepository;
    private final MoviedbClient movieClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PopulationService self;

    @Autowired
    public PopulationService(MovieRepository movieRepository, ApplicationUserService applicationUserService, ApplicationUserRepository applicationUserRepository, MoviedbClient movieClient) {
        this.movieRepository = movieRepository;
        this.applicationUserService = applicationUserService;
        this.applicationUserRepository = applicationUserRepository;
        this.movieClient = movieClient;
    }

    @Async
    public void populateAll() throws IOException {
        self.addUsers();
        self.addMovies();
        self.addRatings();
        self.calculateVotesAndAverage();
    }

    @Transactional
    public void addMovies() throws IOException {
        log.info("Population: adding movies...");
        parseCSV(linksFile, s -> {
            if (!s.isEmpty()) {
                String[] tokens = s.split(CSV_DELIMITER);
                Response response = movieClient.getMovieById(Long.parseLong(tokens[2]));

                if (HttpStatus.valueOf(response.status()).is2xxSuccessful()) {
                    Map<String, Collection<String>> headers = response.headers();

                    try {
                        applyPressureBackoff(headers);

                        MoviedbGetMovieDto movieDbDto =
                                objectMapper.readValue(response.body().asInputStream(), MoviedbGetMovieDto.class);

                        List<Genre> genreList = movieDbDto.getGenres().stream()
                                .map(genre -> Genre.builder()
                                        .genreId(genre.getId())
                                        .name(genre.getName())
                                        .build())
                                .collect(Collectors.toList());

                        Movie movieToSave = createMovieEntity(movieDbDto, genreList);

                        movieRepository.save(movieToSave);
                    } catch (ParseException | IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    private void applyPressureBackoff(Map<String, Collection<String>> headers) throws InterruptedException {
        if (headers.get(HEADER_LIMIT_REMAINING) != null && headers.get(HEADER_LIMIT_RESET) != null) {
            Optional<String> remainingRequestLimit = headers.get(HEADER_LIMIT_REMAINING).stream().findFirst();
            Optional<String> remainingLimitReset = headers.get(HEADER_LIMIT_RESET).stream().findFirst();
            if (remainingRequestLimit.isPresent() && remainingLimitReset.isPresent()) {
                if (Integer.parseInt(remainingRequestLimit.get()) <= 0) {
                    Thread.sleep(
                            Math.abs(Long.parseLong(remainingLimitReset.get())
                                    - Instant.now().getEpochSecond()
                                    + 1
                            ) * 1000
                    );
                }
            }
        }
    }

    private Movie createMovieEntity(MoviedbGetMovieDto movie, List<Genre> genreList) throws ParseException {
        return Movie.builder()
                .movieId(movie.getId())
                .title(movie.getTitle())
                .overview(movie.getOverview())
                .posterPath(movie.getPosterPath())
                .releaseDate(DateUtils.stringToDate(movie.getReleaseDate()))
                .originalLanguage(movie.getOriginalLanguage())
                .genre(genreList)
                .build();
    }

    void addRatings() throws IOException {
        log.info("Population: adding ratings...");
        parseCSV(ratingsFile, s -> {
            String[] tokens = s.split(CSV_DELIMITER);
            String userId = tokens[0];
            Double ratingDouble = Double.parseDouble(tokens[2]);
            int stars = ratingDouble.intValue() * 2;
            long movieId = Long.parseLong(tokens[4]);
            String userEmail = createMockUserEmail(userId);

            self.addRating(movieId, userEmail, stars);
        });
    }

    @Transactional
    public void addRating(long movieId, String userEmail, int stars) {
        Optional<Movie> optionalMovie = movieRepository.findById(movieId, 0);
        UserWithoutRatingsDto userWithoutRatings = applicationUserRepository.findUserWithoutRatingsDtoProjectionByEmail(userEmail);
        if (optionalMovie.isPresent() && userWithoutRatings != null) {
            Rating rating = movieRepository.findRatingByMovieIdAndUserEmail(movieId, userEmail);
            if (rating == null) {
                movieRepository.saveRating(
                        movieId, userEmail, stars, new Date(),
                        RatingIdStrategy.generateRatingId(
                                UUID.fromString(userWithoutRatings.getId()), movieId
                        )
                );
            }
        }
    }

    void calculateVotesAndAverage() throws IOException {
        log.info("Population: calculating user votes and movie averages...");
        parseCSV(linksFile, s -> {
            String[] tokens = s.split(CSV_DELIMITER);
            self.calculateVoteAndAverageByMovieId(Long.parseLong(tokens[2]));
        });
    }

    @Transactional
    public void calculateVoteAndAverageByMovieId(long movieId) {
        boolean movieExists = movieRepository.existsById(movieId);
        if (movieExists) {
            movieRepository.saveVotesAndAverage(movieId);
        }
    }

    void addUsers() throws IOException {
        log.info("Population: adding users...");
        File file = new ClassPathResource(ratingsFile).getFile();
        try (Stream<String> lines = Files.lines(file.toPath())) {
            Set<String> userIDs = lines.skip(1)
                    .map(line -> {
                        String[] tokens = line.split(CSV_DELIMITER);
                        return tokens[0];
                    })
                    .collect(Collectors.toSet());
            for (String userID : userIDs) {
                self.createMockUser(createMockUserEmail(String.valueOf(userID)));
            }
        }
    }

    private String createMockUserEmail(String userId) {
        return "user_" + userId + "@moviescramble.com";
    }

    @Transactional
    public void createMockUser(String userEmail) {
        Optional<ApplicationUser> optionalApplicationUser = applicationUserRepository.findByEmail(userEmail);
        if (!optionalApplicationUser.isPresent()) {
            UserCreateDto userCreateDto = UserCreateDto.builder()
                    .country("GR")
                    .password("123456")
                    .matchingPassword("123456")
                    .sex(Sex.NON_DISCLOSED)
                    .email(userEmail)
                    .build();

            applicationUserService.createUser(userCreateDto);
        }
    }
}