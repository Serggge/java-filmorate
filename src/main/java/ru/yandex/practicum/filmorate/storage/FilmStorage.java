package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {

    Film save(Film film);

    Optional<Film> findById(long id);

    List<Film> findAllById(Collection<Long> ids);

    List<Film> findAll();

    boolean existsById(long id);

    void deleteAll();

    List<Long> findBySubString(String substring);

    List<Long> findByParams(Map<String, String> allParams);

    List<Long> findAllIds();

    void delete(long id);

}
