package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FilmReleaseValidation.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FilmReleaseDate {
    String message() default "Film before 1895";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
