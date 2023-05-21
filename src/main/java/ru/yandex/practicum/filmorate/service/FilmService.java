package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    Film create(Film film);

    Film update(Film film);

    List<Film> getAll();

    Film getById(long id);

    Film setLike(long filmId, long userId);

    Film deleteLike(long filmId, long userId);

    List<Film> getPopular(int count);

    List<Film> getDirectorFilms(int directorId, String sortBy);

}
