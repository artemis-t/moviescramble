package com.moviescramble.user.repository;

import com.moviescramble.user.domain.ApplicationUser;
import com.moviescramble.user.dto.UserListingDto;
import com.moviescramble.user.dto.UserWithoutRatingsDto;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationUserRepository extends Neo4jRepository<ApplicationUser, UUID> {

    Optional<ApplicationUser> findByEmail(String email);

    UserListingDto findUserListingDtoProjectionById(UUID id);

    @Query("MATCH (u:user)-[r:LIVES_IN]->(c:country) WHERE u.email={0} " +
            "RETURN u.password AS password, u.role AS role, u.email AS email, u.sex AS sex,u.birthYear AS birthYear, u.id AS id, c AS country")
    UserWithoutRatingsDto findUserWithoutRatingsDtoProjectionByEmail(String userEmail);
}
