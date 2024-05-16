package ru.yandex.practicum.filmorate.exception;

public class ValidationExceptionInController extends RuntimeException {
    public ValidationExceptionInController(String str) {
        super(str);
    }
}
