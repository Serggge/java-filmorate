package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    Film save(Film film);

    Optional<Film> findById(long id);

    List<Film> findAll();

    Set<Long> findAllId();

}
