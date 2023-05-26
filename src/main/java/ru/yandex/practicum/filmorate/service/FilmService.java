package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Map;

public interface FilmService {

    Film create(Film film);

    Film update(Film film);

    List<Film> getAll();

    Film getById(long id);

    void setLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    List<Film> getPopular(Map<String, String> allParams);

    List<Film> getSortedFilms(int directorId, String sortBy);

    void delete(long filmId);

    List<Film> getCommonFilmPopular(long userId, long friendId);
    List<Film> getRecommendedFilms(long userId);

    List<Film> searchByParams(String query, List<String> by);

}
