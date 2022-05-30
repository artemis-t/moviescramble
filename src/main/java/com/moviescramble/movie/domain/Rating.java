package com.moviescramble.movie.domain;

import com.moviescramble.user.domain.ApplicationUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.util.Date;

@RelationshipEntity(type = "RATED")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(exclude = {"stars"})
public class Rating {

    @Id
    @GeneratedValue(strategy = RatingIdStrategy.class)
    private String id;

    @StartNode
    private ApplicationUser user;

    @EndNode
    private Movie movie;

    @Property
    private int stars;

    @Property
    @DateLong
    private Date ratingDate;
}
