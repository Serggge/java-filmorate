package ru.yandex.practicum.filmorate.exception;

public class ReviewNotFoundException extends EntityNotFoundException {

    public ReviewNotFoundException(String message) {
        super(message);
    }

}
