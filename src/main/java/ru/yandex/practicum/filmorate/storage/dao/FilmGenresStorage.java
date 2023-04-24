package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

public interface FilmGenresStorage {

    Film save(Film film);

    Iterable<Genre> findAllById(long id);

    void deleteById(long id);

}
