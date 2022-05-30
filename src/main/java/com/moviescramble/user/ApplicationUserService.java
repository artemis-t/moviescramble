package com.moviescramble.user;

import com.moviescramble.country.Country;
import com.moviescramble.country.CountryRepository;
import com.moviescramble.global.customexceptions.MoviescrambleException;
import com.moviescramble.global.securityconfig.JwtAuthenticationResponse;
import com.moviescramble.global.securityconfig.JwtTokenProvider;
import com.moviescramble.global.securityconfig.UserPrincipal;
import com.moviescramble.user.domain.ApplicationUser;
import com.moviescramble.user.domain.RoleName;
import com.moviescramble.user.dto.UserCreateDto;
import com.moviescramble.user.dto.UserListingDto;
import com.moviescramble.user.dto.UserLoginDto;
import com.moviescramble.user.dto.UserUpdateDto;
import com.moviescramble.user.repository.ApplicationUserRepository;
import com.moviescramble.utils.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final CountryRepository countryRepository;

    @Autowired
    public ApplicationUserService(ApplicationUserRepository applicationUserRepository, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, CountryRepository countryRepository) {
        this.applicationUserRepository = applicationUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.countryRepository = countryRepository;
    }

    @Transactional
    public ApplicationUser createUser(UserCreateDto userCreateDto) {
        Optional<ApplicationUser> existingUser = applicationUserRepository.findByEmail(userCreateDto.getEmail());
        if (existingUser.isPresent()) {
            throw new MoviescrambleException("Email already in use", HttpStatus.BAD_REQUEST);
        }

        Optional<Country> countryOptional = countryRepository.findByIsoCode(userCreateDto.getCountry());
        if (!countryOptional.isPresent()) {
            throw new MoviescrambleException("Requested country not available", HttpStatus.BAD_REQUEST);
        }

        ApplicationUser user = ApplicationUser.builder()
                .email(userCreateDto.getEmail())
                .password(passwordEncoder.encode(userCreateDto.getPassword()))
                .birthYear(userCreateDto.getBirthYear())
                .sex(userCreateDto.getSex())
                .country(countryOptional.get())
                .role(RoleName.ROLE_USER)
                .build();

        return applicationUserRepository.save(user);
    }

    @Transactional
    public void updateUser(UserUpdateDto userUpdateDto, UUID userId) {
        Optional<ApplicationUser> userOptional = applicationUserRepository.findById(userId);

        if (!userOptional.isPresent()) {
            throw new MoviescrambleException("User was not found", HttpStatus.NOT_FOUND);
        }

        Optional<Country> countryOptional = countryRepository.findByIsoCode(userUpdateDto.getCountry());
        if (!countryOptional.isPresent()) {
            throw new MoviescrambleException("Requested country not available", HttpStatus.BAD_REQUEST);
        }

        ApplicationUser updatedUser = userOptional.get().toBuilder()
                .birthYear(userUpdateDto.getBirthYear())
                .sex(userUpdateDto.getSex())
                .country(countryOptional.get())
                .build();
        applicationUserRepository.save(updatedUser);

    }

    @Transactional
    public ResponseEntity<?> authenticateUser(UserLoginDto userLoginDto) throws ParseException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginDto.getUsername(),
                        userLoginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generate(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    public UserListingDto findUserById(UUID userId) {
        UserListingDto user = applicationUserRepository.findUserListingDtoProjectionById(userId);
        if (user == null) {
            throw new MoviescrambleException("Logged-in user not found", HttpStatus.FORBIDDEN);
        }
        return user;
    }

    public ApplicationUser getAuthenticatedUser() {
        UserPrincipal userPrincipal = SpringSecurityUtils.getUserPrincipal();
        Optional<ApplicationUser> optionalUser = applicationUserRepository.findById(userPrincipal.getId());
        if (!optionalUser.isPresent()) {
            throw new MoviescrambleException("User was not found", HttpStatus.NOT_FOUND);
        }
        return optionalUser.get();
    }
}