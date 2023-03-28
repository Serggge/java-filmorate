package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorMessage;

import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({UserNotFoundException.class, FilmNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage userNotFoundException(RuntimeException exception) {
        return defaultNotFoundMessage(exception);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage validationException(ValidationException exception) {
        return ErrorMessage.builder()
                           .statusCode(400)
                           .httpStatus(HttpStatus.BAD_REQUEST)
                           .timeStamp(Instant.now())
                           .message(exception.getMessage())
                           .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage argumentNotValidException(MethodArgumentNotValidException exception) {
        return ErrorMessage.builder()
                           .statusCode(400)
                           .httpStatus(HttpStatus.BAD_REQUEST)
                           .timeStamp(Instant.now())
                           .message(exception.getMessage())
                           .description("Параметры запроса не удовлетворяют условиям объекта")
                           .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleIncorrectParam(IncorrectParameterException e) {
        return ErrorMessage.builder()
                           .statusCode(400)
                           .httpStatus(HttpStatus.BAD_REQUEST)
                           .timeStamp(Instant.now())
                           .message(
                                   String.format("Некорректный параметр %s", e.getParam())
                           )
                           .description(e.getDescription())
                           .build();
    }

    private static ErrorMessage defaultNotFoundMessage(Exception exception) {
        return ErrorMessage.builder()
                           .statusCode(404)
                           .httpStatus(HttpStatus.NOT_FOUND)
                           .timeStamp(Instant.now())
                           .message(exception.getMessage())
                           .build();
    }

}
