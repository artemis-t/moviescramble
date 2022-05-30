package com.moviescramble.global.customannotations;

import com.moviescramble.user.dto.UserCreateDto;
import com.moviescramble.user.dto.UserUpdateDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserDetailsValidator implements ConstraintValidator<UserDetails, UserCreateDto> {

    private boolean emptyAllowed = false;

    @Override
    public void initialize(UserDetails userDetails) {
        Class<UserDetails.AllowEmpty>[] groups = userDetails.groups();
        if (groups.length > 0) {
            emptyAllowed = true;
        }
    }

    @Override
    public boolean isValid(UserCreateDto userCreateDto, ConstraintValidatorContext constraintValidatorContext) {
        String password = userCreateDto.getPassword();
        String matchingPassword = userCreateDto.getMatchingPassword();
        if (!emptyAllowed) {
            return password != null && !password.isEmpty()
                    && password.equals(matchingPassword);
        }

        return emptyAllowed;
    }
}
