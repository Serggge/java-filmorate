package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;

public interface GenreService {

    Genre create(Genre genre);

    Genre update(Genre genre);

    List<Genre> getAll();

    Genre getById(long id);

}
