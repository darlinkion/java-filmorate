package ru.yandex.practicum.filmorate.model;

import jakarta.validation.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTestValidate {

    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    void setEmail() {
        User user = User.builder()
                .email("qifqef.ru")
                .login("user")
                .name("Igor")
                .birthday(LocalDate.of(2018, 3, 6))
                .build();

        Set<ConstraintViolation<User>> validatorSet = validator.validate(user);
        assertFalse(validatorSet.isEmpty());
    }

    @Test
    void setLogin() {
        User user = User.builder()
                .email("Nagibator1999@mail.ru")
                .login("")
                .name("Igor")
                .birthday(LocalDate.of(2008, 3, 6))
                .build();

        Set<ConstraintViolation<User>> validatorSet = validator.validate(user);
        assertFalse(validatorSet.isEmpty());
    }


    @Test
    void setBirthday() {
        User user = User.builder()
                .email("Nagibator1999@mail.ru")
                .login("User")
                .name("Igor")
                .birthday(LocalDate.of(2092, 2, 12))
                .build();

        Set<ConstraintViolation<User>> validatorSet = validator.validate(user);
        assertFalse(validatorSet.isEmpty());
    }
}