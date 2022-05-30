package com.moviescramble.nlp;

import com.moviescramble.movie.domain.Movie;
import com.moviescramble.movie.repository.MovieRepository;
import com.moviescramble.nlp.domain.HasLemma;
import com.moviescramble.nlp.domain.Lemma;
import com.moviescramble.recommend.RecommendMovieDto;
import com.moviescramble.recommend.RecommendRepository;
import com.moviescramble.user.ApplicationUserService;
import com.moviescramble.user.dto.UserListingDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.moviescramble.utils.SpringSecurityUtils.getUserPrincipal;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

@Slf4j
@Service
public class NlpService {
    private final MovieRepository movieRepository;
    private final StanfordCoreNLP pipeline;
    private final IgnoreWordService ignoreWordService;
    private final ApplicationUserService applicationUserService;
    private final RecommendRepository recommendRepository;

    @Autowired
    private NlpService self;

    @Autowired
    public NlpService(MovieRepository movieRepository,
                      IgnoreWordService ignoreWordService, ApplicationUserService applicationUserService, RecommendRepository recommendRepository) {
        this.movieRepository = movieRepository;
        this.ignoreWordService = ignoreWordService;
        this.applicationUserService = applicationUserService;
        this.recommendRepository = recommendRepository;
        Properties properties = new Properties();
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        pipeline = new StanfordCoreNLP(properties);
    }

    @Async
    public void processAll() {
        self.createLemmasFromAllOverviews();
        self.calculateTfIdfForAllOverviews();
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private Session session;

    public void createLemmasFromAllOverviews() {
        log.info("NLP: processing lemmas for all movie overviews...");
        while (true) {

            Page<Movie> movies = movieRepository.findMovieByProcessed(false, PageRequest.of(0, 20));

            if (movies.getTotalElements() == 0) {
                break;
            }

            movies.forEach(movie -> {
                Set<Lemma> lemmaSetFromOverview = new HashSet<>();
                if (!StringUtils.isEmpty(movie.getOverview())) {
                    createLemmasForMovie(movie, lemmaSetFromOverview);
                    movie.setLemmaRels(lemmaSetFromOverview.stream()
                            .map(lemma -> HasLemma.builder()
                                    .lemma(lemma)
                                    .movie(movie)
                                    .build()
                            )
                            .collect(Collectors.toSet())
                    );
                }

                movie.setProcessed(true);
                movieRepository.save(movie);
            });

            session.clear();
        }
    }

    void calculateTfIdfForAllOverviews() {
        log.info("NLP: calculating TF/IDF for all movie overviews...");

        int page = 0;

        Long movieCount = movieRepository.countAllByOverviewExists();

        while (true) {
            Page<Movie> movies = movieRepository.findMovieWithoutTfidf(true, PageRequest.of(page, 20));
            if (movies.getTotalElements() == 0) {
                break;
            }

            for (Movie movie : movies) {
                if (StringUtils.isEmpty(movie.getOverview())) {
                    continue;
                }

                List<Lemma> lemmasList = new ArrayList<>();
                createLemmasForMovie(movie, lemmasList);
                int numberOfLemmas = lemmasList.size();

                Map<Lemma, Integer> countPerLemma = lemmasList.stream()
                        .collect(
                                groupingBy(Function.identity(), summingInt(lemma -> 1))
                        );

                List<LemmaTfIdf> lemmaTfIdfs = countPerLemma
                        .entrySet()
                        .stream()
                        .map(entry -> {
                            Lemma lemma = entry.getKey();
                            Integer count = entry.getValue();

                            double tf = 1.0 * count / numberOfLemmas;

                            Long moviesWithLemma = movieRepository.countMoviesWithLemma(lemma.getText());

                            double idf = Math.log(1.0 * movieCount / (1 + moviesWithLemma));

                            return LemmaTfIdf.builder()
                                    .lemma(lemma.getText())
                                    .tfidf(tf * idf)
                                    .build();
                        })
                        .sorted()
                        //.limit(30)
                        .collect(Collectors.toList());

                Map<String, List<LemmaTfIdf>> parameters = new HashMap<>();
                parameters.put("entries", lemmaTfIdfs);

                String cypher = "UNWIND $entries AS entry " +
                        "MATCH(m:movie)-[r:HAS_LEMMA]->(l:lemma) " +
                        "WHERE m.movieId=" + movie.getMovieId() + " and l.text=entry.lemma " +
                        "SET r.tfidf=entry.tfidf";

                session.query(cypher, parameters);
            }
        }
    }

    public List<Movie> getRecommendedMovies(long movieId, double threshold) {
        // TODO pageable

        return recommendRepository.getSimilarMovies(movieId, threshold)
                .stream()
                .map(RecommendMovieDto::getM)
                .collect(Collectors.toList());
    }

    public List<Movie> getRecommendedMoviesForUser(int ratingThreshold, double similarityThreshold) {
        UserListingDto user = applicationUserService.findUserById(getUserPrincipal().getId());

        // TODO pageable
        return recommendRepository.getRecommendedMoviesForUser(user.getId(), ratingThreshold, similarityThreshold)
                .stream()
                .map(RecommendMovieDto::getM)
                .collect(Collectors.toList());
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @EqualsAndHashCode
    @ToString
    static class LemmaTfIdf implements Comparable<LemmaTfIdf> {
        private String lemma;
        private double tfidf;

        @Override
        public int compareTo(@NotNull LemmaTfIdf other) {
            return Double.compare(other.tfidf, tfidf);
        }
    }

    private void createLemmasForMovie(Movie movie, Collection<Lemma> collection) {
        CoreDocument doc = new CoreDocument(movie.getOverview());
        pipeline.annotate(doc);
        for (CoreLabel token : doc.tokens()) {
            Optional<Lemma> lemma = createLemmaFromToken(token);
            lemma.ifPresent(collection::add);
        }
    }

    private Optional<Lemma> createLemmaFromToken(CoreLabel token) {
        switch (token.ner()) {
            case "PERSON":
            case "NUMBER":
            case "ORDINAL":
            case "DATE":
            case "DURATION":
            case "MONEY":
                break;
            default:
                if (!ignoreWordService.isIgnored(token)) {
                    return Optional.of(Lemma.builder()
                            .text(token.lemma().toLowerCase())
                            .build());
                }
        }
        return Optional.empty();
    }
}
