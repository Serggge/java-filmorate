package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.ErrorMessage;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    private static final ErrorMessage errorMessage = new ErrorMessage();

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorMessage handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        errorMessage.setParams("Получен некорректный формат JSON", exception.getMessage());
        log(exception);
        return errorMessage;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorMessage handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        Map<String, String> errorReport = new HashMap<>();
        exception.getBindingResult()
                 .getAllErrors()
                 .forEach(error -> {
                     String fieldName = ((FieldError) error).getField();
                     String message = error.getDefaultMessage();
                     errorReport.put(fieldName, message);
                 });
        errorMessage.setParams(errorReport.keySet().toString(), errorReport.values().toString());
        log(exception);
        return errorMessage;
    }

    @ExceptionHandler({UserNotFoundException.class, FilmNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFoundException(RuntimeException exception) {
        errorMessage.setParams(exception.getMessage(), "");
        log(exception);
        return errorMessage;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleValidationException(ValidationException exception) {
        errorMessage.setParams(exception.getMessage(), "");
        log(exception);
        return errorMessage;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String message = String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                                        exception.getName(), exception.getValue(), exception.getRequiredType());
        errorMessage.setParams(message, exception.getMessage());
        log(exception);
        return errorMessage;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleIncorrectParamException(IncorrectParameterException exception) {
        errorMessage.setParams(exception.getParam(), exception.getDescription());
        log(exception);
        return errorMessage;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorMessage handleDataUpdateException(DataUpdateException exception) {
        errorMessage.setParams(exception.getMessage(), "");
        log(exception);
        return errorMessage;
    }

    private void log(Exception e) {
        log.warn("{} : {} >> {}", e.getClass().getSimpleName(),
                errorMessage.getMessage(), errorMessage.getDescription());
    }

}
