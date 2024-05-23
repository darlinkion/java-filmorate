package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

class FilmTestValidate {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void setName() {
        Film film = Film.builder()
                .name("")
                .description("Best film")
                .releaseDate(LocalDate.of(1999,05,29))
                .duration(10)
                .build();

        Set<ConstraintViolation<Film>> validatorSet = validator.validate(film);
        assertFalse(validatorSet.isEmpty());
    }

    @Test
    void setDescription() {
        Film film = Film.builder()
                .name("Матрица")
                .description("")
                .releaseDate(LocalDate.of(1999,05,29))
                .duration(10)
                .build();

        Set<ConstraintViolation<Film>> validatorSet = validator.validate(film);
        assertFalse(validatorSet.isEmpty());
    }

    @Test
    void setReleaseDate() {
        Film film = Film.builder()
                .name("Матрица")
                .description("Жизнь Томаса Андерсона разделена на две части")
                .releaseDate(LocalDate.of(1500,05,29))
                .duration(100)
                .build();

        Set<ConstraintViolation<Film>> validatorSet = validator.validate(film);
        assertFalse(validatorSet.isEmpty());
    }

    @Test
    void setDuration() {
        Film film = Film.builder()
                .name("Матрица")
                .description("Жизнь Томаса Андерсона разделена на две части")
                .releaseDate(LocalDate.of(1999,05,29))
                .duration(-100)
                .build();

        Set<ConstraintViolation<Film>> validatorSet = validator.validate(film);
        assertFalse(validatorSet.isEmpty());
    }
}