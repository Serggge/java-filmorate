package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.Collection;

public interface FilmGenreStorage {

    Film save(Film film);

    Collection<Genre> findAllById(long id);

    void deleteById(long id);

}
