package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;
import java.util.Optional;

public interface GenreStorage {

    Genre save(Genre genre);

    Optional<Genre> findById(long id);

    List<Genre> findAll();

    boolean existsById(long id);

}
