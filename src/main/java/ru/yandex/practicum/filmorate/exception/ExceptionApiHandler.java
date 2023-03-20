package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.time.Instant;

@RestControllerAdvice
public class ExceptionApiHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> userNotFoundException(UserNotFoundException exception) {
        ErrorMessage errorMessage = defaultNotFoundMessage(exception);
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FilmNotFoundException.class)
    public ResponseEntity<ErrorMessage> filmNotFoundException(FilmNotFoundException exception) {
        ErrorMessage errorMessage = defaultNotFoundMessage(exception);
        return new ResponseEntity<>(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorMessage> validationException(ValidationException exception) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .statusCode(400)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .timeStamp(Instant.now())
                .message(exception.getMessage())
                .build();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> argumentNotValidException(MethodArgumentNotValidException exception) {
        DefaultHandlerExceptionResolver resolver;
        ErrorMessage errorMessage = ErrorMessage.builder()
                .statusCode(400)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .timeStamp(Instant.now())
                .message(exception.getMessage())
                .description("Параметры запроса не удовлетворяют условиям оъекта")
                .build();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
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
