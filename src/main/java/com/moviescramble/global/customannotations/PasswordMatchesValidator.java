package com.moviescramble.global.customannotations;

import com.moviescramble.user.dto.UserPasswordResetDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserPasswordResetDto> {
    @Override
    public void initialize(PasswordMatches passwordMatches) {
    }

    @Override
    public boolean isValid(UserPasswordResetDto userPasswordResetDto, ConstraintValidatorContext constraintValidatorContext) {
        return !userPasswordResetDto.getCurrentPassword().equals(userPasswordResetDto.getNewPassword())
                && userPasswordResetDto.getNewPassword().equals(userPasswordResetDto.getPasswordMatch());
    }
}
