package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DirectorsStorage {

    List<Director> getAllDirectors();

    Director getDirectorById(int id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(int id);

    Film save(Film film);

    List<Director> findDirectorsByFilmId(long id);

    Map<Long, Set<Director>> findAll(Collection<Long> ids);

    void deleteByFilmId(long id);

    SqlRowSet getDirectorInFilms(int directorId);
}
