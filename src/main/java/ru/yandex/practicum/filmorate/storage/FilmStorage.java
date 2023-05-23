package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film save(Film film);

    Film update(Film film);

    Optional<Film> findById(long id);

    List<Film> findAllById(Collection<Long> ids);

    List<Film> findAll();

    boolean existsById(long id);

    void deleteAll();

    List<Long> findBySubString(String substring);

    List<Long> findAllByYear(int year);

    List<Long> findAllByGenre(int genreId);

    List<Long> findAllIds();

    void deleteFilm(long id);

}
