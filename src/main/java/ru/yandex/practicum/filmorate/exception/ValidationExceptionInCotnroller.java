package ru.yandex.practicum.filmorate.exception;

public class ValidationExceptionInCotnroller extends RuntimeException {
    public ValidationExceptionInCotnroller(String str) {
        super(str);
    }
}
