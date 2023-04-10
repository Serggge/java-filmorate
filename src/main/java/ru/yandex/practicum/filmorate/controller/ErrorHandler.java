package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.ErrorResponse;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    private static final ErrorResponse errorResponse = new ErrorResponse();

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        errorResponse.setParams("Получен некорректный формат JSON", exception.getMessage());
        log(exception);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        Map<String, String> errorReport = new HashMap<>();
        exception.getBindingResult()
                 .getAllErrors()
                 .forEach(error -> {
                     String fieldName = ((FieldError) error).getField();
                     String message = error.getDefaultMessage();
                     errorReport.put(fieldName, message);
                 });
        errorResponse.setParams(errorReport.keySet().toString(), errorReport.values().toString());
        log(exception);
        return errorResponse;
    }

    @ExceptionHandler({UserNotFoundException.class, FilmNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(RuntimeException exception) {
        errorResponse.setParams(exception.getMessage(), "");
        log(exception);
        return errorResponse;
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException exception) {
        errorResponse.setParams(exception.getMessage(), "");
        log(exception);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String message = String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                                        exception.getName(), exception.getValue(), exception.getRequiredType());
        errorResponse.setParams(message, exception.getMessage());
        log(exception);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponse handleDataUpdateException(DataUpdateException exception) {
        errorResponse.setParams(exception.getMessage(), "");
        log(exception);
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleHttpRequestMethodNotSupportedEx(HttpRequestMethodNotSupportedException exception) {
        errorResponse.setParams(exception.getMessage(), exception.getMethod());
        log(exception);
        return errorResponse;
    }

    private void log(Exception e) {
        log.warn("{} : {} >> {}", e.getClass().getSimpleName(),
                errorResponse.getMessage(), errorResponse.getDescription());
    }

}
