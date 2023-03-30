package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import static ru.yandex.practicum.filmorate.Constants.FIRST_FILM;

public final class Validator {

    private Validator() {

    }

    public static long validateId(String stringId) {
        try {
            return Long.parseLong(stringId);
        } catch (NumberFormatException e) {
            throw new IncorrectParameterException("id", "Идентификатор не числовой");
        }
    }

    public static void validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public static void validateFilm(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate.isBefore(FIRST_FILM)) {
            throw new ValidationException(String.format("Дата релиза фильма раньше %s", FIRST_FILM));
        }
    }

}
