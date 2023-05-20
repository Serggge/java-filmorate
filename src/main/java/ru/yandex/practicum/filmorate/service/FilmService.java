package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Map;

public interface FilmService {

    Film create(Film film);

    Film update(Film film);

    List<Film> getAll();

    Film getById(long id);

    Film setLike(long filmId, long userId);

    Film deleteLike(long filmId, long userId);

    List<Film> getPopular(Map<String, String> allParams);

    void delete(long filmId);

    List<Film> getRecommendedFilms(long userId);

    List<Film> searchByParams(String query, List<String> by);

}
