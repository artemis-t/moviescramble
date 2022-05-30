package com.moviescramble.movie.domain;

import com.moviescramble.genre.domain.Genre;
import com.moviescramble.nlp.domain.HasLemma;
import com.moviescramble.user.domain.ApplicationUser;
import com.moviescramble.utils.CollectionUtils;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity(label = "movie")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"movieId", "title", "releaseDate"})
public class Movie {

    @Id
    private Long movieId;

    @Property
    private String title;

    @Property
    private String originalTitle;

    @Property
    private String originalLanguage;

    @Property
    private boolean adult;

    @Property
    private boolean video;

    @Property
    private String overview;

    @Property
    private Date releaseDate;

    @Property
    private double popularity;

    @Property
    private int votes;

    @Property
    private double voteAverage;

    @Property
    private String posterPath;

    @Relationship(type = "HAS_GENRE")
    private List<Genre> genre;

    @Relationship(type = "RATED", direction = Relationship.INCOMING)
    private Set<Rating> ratings;
    //
    //@Relationship(type = "HAS_LEMMA")
    //private Set<Lemma> lemmas;

    @Relationship(type = "HAS_LEMMA")
    private Set<HasLemma> lemmaRels;

    private boolean processed;

    public void addRating(Rating rating) {
        ratings = CollectionUtils.addEntryToSet(rating, ratings);
    }

    public Rating getExistingRating(ApplicationUser applicationUser) {
        if (this.getRatings() == null) {
            return null;
        }

        Optional<Rating> optionalRating = this.getRatings()
                .stream()
                .filter(rating -> rating.getUser().equals(applicationUser))
                .findFirst();

        return optionalRating.orElse(null);
    }
}
