package ru.yandex.practicum.filmorate.exception;

public class ValidationException extends RuntimeException {

    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(Throwable throwable) {
        super(throwable);
    }

    public ValidationException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
