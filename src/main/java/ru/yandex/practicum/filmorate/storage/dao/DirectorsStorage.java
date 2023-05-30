package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DirectorsStorage {

    List<Director> getAll();

    Director getById(int id);

    Director create(Director director);

    Director update(Director director);

    void delete(int id);

    Film save(Film film);

    List<Director> findDirectorsByFilmId(long id);

    Map<Long, Set<Director>> findAll(Collection<Long> ids);

    void deleteByFilmId(long id);

    List<Long> getSortedFilms(int directorId);

    List<Long> findBySubString(String substring);

    boolean existsById(int directorId);

}
