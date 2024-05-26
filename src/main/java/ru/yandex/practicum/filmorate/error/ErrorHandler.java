package ru.yandex.practicum.filmorate.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(value = ValidationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse validationExceptionInController(final ValidationException e) {
        log.info("Validation {}" + "\n" + getStackTrace(e), e.getMessage());
        return new ErrorResponse(e.getMessage(), getStackTrace(e));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final Exception e) {
        log.warn("Error\n" + getStackTrace(e), getStackTrace(e));
        return new ErrorResponse("Необработанная ошибка", getStackTrace(e));
    }

    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
