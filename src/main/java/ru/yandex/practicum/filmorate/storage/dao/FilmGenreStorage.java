package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface FilmGenreStorage {

    Film save(Film film);

    List<Genre> findGenresByFilmId(long id);

    void deleteByFilmId(long id);

    Map<Long, Set<Genre>> findAll(Collection<Long> ids);

    void deleteAll();

}
