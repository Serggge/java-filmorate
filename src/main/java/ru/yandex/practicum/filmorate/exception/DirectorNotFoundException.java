package ru.yandex.practicum.filmorate.exception;

public class DirectorNotFoundException extends  EntityNotFoundException {

    public DirectorNotFoundException(String message) {
        super(message);
    }

}

