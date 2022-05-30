package com.moviescramble.user;

import com.moviescramble.user.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.UUID;

import static com.moviescramble.utils.SpringSecurityUtils.getUserPrincipal;

@RestController
public class ApplicationUserController {

    private final ApplicationUserService applicationUserService;

    @Autowired
    public ApplicationUserController(ApplicationUserService applicationUserService) {
        this.applicationUserService = applicationUserService;
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @RequestMapping(value = "/users/register", method = RequestMethod.POST)
    public void register(@Valid @RequestBody UserCreateDto userCreateDto) {
        applicationUserService.createUser(userCreateDto);
    }

    @ResponseStatus(code = HttpStatus.OK)
    @RequestMapping(value = "/users/{userId}", method = RequestMethod.PUT)
    public void update(
            @Valid @RequestBody UserUpdateDto userUpdateDto,
            @PathVariable("userId") UUID userId) {
        applicationUserService.updateUser(userUpdateDto, userId);
    }

    @ResponseStatus(code = HttpStatus.OK)
    @RequestMapping(value = "/users/{userId}/change-password", method = RequestMethod.PUT)
    public void editPassword(@Valid @RequestBody UserPasswordResetDto userPasswordResetDto) {

    }

    @RequestMapping(value = "/users/login", method = RequestMethod.POST)
    public ResponseEntity<?> userLogin(@Valid @RequestBody UserLoginDto userLoginDto) throws ParseException {
        return applicationUserService.authenticateUser(userLoginDto);
    }

    @RequestMapping(value = "/users/me", method = RequestMethod.GET)
    public UserListingDto getMyDetails() {
        return applicationUserService.findUserById(getUserPrincipal().getId());
    }
}
