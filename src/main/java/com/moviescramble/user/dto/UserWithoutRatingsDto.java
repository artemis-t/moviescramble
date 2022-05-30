package com.moviescramble.user.dto;

import com.moviescramble.country.Country;
import com.moviescramble.user.domain.RoleName;
import com.moviescramble.user.domain.Sex;
import lombok.*;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserWithoutRatingsDto {

    String id;

    String password;

    String email;

    int birthYear;

    Sex sex;

    Country country;

    RoleName role;
}
