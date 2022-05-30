package com.moviescramble.global.customannotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserDetailsValidator.class)
@Documented
public @interface UserDetails {
    interface AllowEmpty {}

    String message() default "Passwords do not match";

    Class<AllowEmpty>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
