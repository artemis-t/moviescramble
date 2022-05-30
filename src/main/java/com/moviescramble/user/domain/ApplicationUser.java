package com.moviescramble.user.domain;

import com.moviescramble.country.Country;
import com.moviescramble.movie.domain.Rating;
import com.moviescramble.utils.CollectionUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.id.UuidStrategy;
import org.neo4j.ogm.typeconversion.UuidStringConverter;

import java.util.Set;
import java.util.UUID;

@Builder(toBuilder = true)
@NodeEntity(label = "user")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class ApplicationUser {
    @Id
    @GeneratedValue(strategy = UuidStrategy.class)
    @Convert(value = UuidStringConverter.class)
    private UUID id;

    @Property
    @Index(unique = true)
    private String email;

    @Property
    private String password;

    @Property
    private int birthYear;

    @Property
    private Sex sex;

    @Relationship(type = "LIVES_IN")
    private Country country;

    @Property
    private RoleName role;

    @Relationship(type = "RATED")
    private Set<Rating> ratings;

    public void addRating(Rating rating) {
        ratings = CollectionUtils.addEntryToSet(rating, ratings);
    }
}
