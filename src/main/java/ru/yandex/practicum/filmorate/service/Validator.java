package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import static ru.yandex.practicum.filmorate.util.Constants.FIRST_FILM;

public final class Validator {

    private Validator() {
    }

    public static User validateUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getFriends() == null) {
            return User.builder()
                    .id(user.getId())
                    .login(user.getLogin())
                    .email(user.getEmail())
                    .name(user.getName())
                    .birthday(user.getBirthday())
                    .build();
        } else {
            return user;
        }
    }

    public static Film validateFilm(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate.isBefore(FIRST_FILM)) {
            throw new ValidationException(String.format("Дата релиза фильма раньше %s", FIRST_FILM));
        }
        if (film.getGenres() == null || film.getLikes() == null) {
            return Film.builder()
                    .id(film.getId())
                    .name(film.getName())
                    .description(film.getDescription())
                    .releaseDate(film.getReleaseDate())
                    .duration(film.getDuration())
                    .mpa(film.getMpa())
                    .build();
        } else {
            return film;
        }
    }

    public static void createValidator(Review review) {
        if (review.getReviewId() != 0) {
            throw new ValidationException("Недопустимый параметр ID при создании ревью");
        }
        if (review.getFilmId() == 0) {
            throw new ValidationException("Неверный id фильма: " + review.getFilmId());
        }
        if (review.getUserId() == 0) {
            throw new ValidationException("Неверный id пользователя: " + review.getUserId());
        }
    }

    @NotNull
    public static void validateExistFilm(List<Long> filmIdList, long id) {
        if (!filmIdList.contains(id)) {
            throw new FilmNotFoundException(String.format("Фильм с id %s не найден", id));
        }
    }

    @NotNull
    public static void validateExistUser(List<Long> userIdList, long id) {
        if (!userIdList.contains(id)) {
            throw new FilmNotFoundException(String.format("Пользователь с id %s не найден", id));
        }
    }

    @NotNull
    public static void validateExistReview(List<Long> reviewIdList, long id) {
        if (!reviewIdList.contains(id)) {
            throw new FilmNotFoundException(String.format("Ревью с id %s не найден", id));
        }
    }
}
