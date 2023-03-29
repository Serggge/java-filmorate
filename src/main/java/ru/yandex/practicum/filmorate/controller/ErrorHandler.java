package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.ErrorMessage;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({UserNotFoundException.class, FilmNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFoundException(RuntimeException exception) {
        log(exception);
        return defaultNotFoundMessage(exception);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleValidationException(ValidationException exception) {
        log(exception);
        return ErrorMessage.builder()
                           .statusCode(400)
                           .httpStatus(HttpStatus.BAD_REQUEST)
                           .timeStamp(Instant.now())
                           .message(exception.getMessage())
                           .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage HandleArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errorReport = new HashMap<>();
        exception.getBindingResult()
                 .getAllErrors()
                 .forEach(error -> {
                     String fieldName = ((FieldError) error).getField();
                     String message = error.getDefaultMessage();
                     errorReport.put(fieldName, message);
                 });
        log.warn("{} : Ошибка в параметрах: {}. {}", ValidationException.class.getSimpleName(),
                    errorReport.keySet(), errorReport.values());
        return ErrorMessage.builder()
                           .statusCode(400)
                           .httpStatus(HttpStatus.BAD_REQUEST)
                           .timeStamp(Instant.now())
                           .message(String.format("Ошибка в параметре: %s", errorReport.keySet()))
                           .description(errorReport.values().toString())
                           .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleIncorrectParam(IncorrectParameterException exception) {
        log(exception);
        return ErrorMessage.builder()
                           .statusCode(400)
                           .httpStatus(HttpStatus.BAD_REQUEST)
                           .timeStamp(Instant.now())
                           .message(String.format("Некорректный параметр %s", exception.getParam()))
                           .description(exception.getDescription())
                           .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorMessage handleDataUpdateException(DataUpdateException exception) {
        log(exception);
        return ErrorMessage.builder()
                           .statusCode(406)
                           .httpStatus(HttpStatus.NOT_ACCEPTABLE)
                           .timeStamp(Instant.now())
                           .message(exception.getMessage())
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

    private void log(Exception e) {
        log.warn("{} : {}", e.getClass()
                             .getSimpleName(), e.getMessage());
    }

}
