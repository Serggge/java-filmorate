package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Film save(Film film);

    Optional<Film> findById(long id);

    Collection<Film> findAllById(Collection<Long> ids);

    Collection<Film> findAll();

    boolean existsById(long id);

}
