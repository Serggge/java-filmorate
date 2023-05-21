package ru.yandex.practicum.filmorate.service;

import lombok.SneakyThrows;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

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

    @SneakyThrows
    public static void validateExistFilm(List<Long> filmIdList, Long id) {
        if (id == null) {
            throw new ValidationException("Не указан id фильма");
        }
        if (!filmIdList.contains(id)) {
            throw new FilmNotFoundException(String.format("Фильм с id %s не найден", id));
        }
    }

    @SneakyThrows
    public static void validateExistUser(List<Long> userIdList, Long id) {
        if (id == null) {
            throw new ValidationException("Не указан id пользователя");
        }
        if (!userIdList.contains(id)) {
            throw new FilmNotFoundException(String.format("Пользователь с id %s не найден", id));
        }
    }

    @SneakyThrows
    public static void validateExistReview(List<Long> reviewIdList, Long id) {
        if (id == null) {
            throw new ValidationException("Не указан id отзыва");
        }
        if (!reviewIdList.contains(id)) {
            throw new FilmNotFoundException(String.format("Ревью с id %s не найден", id));
        }
    }
}
