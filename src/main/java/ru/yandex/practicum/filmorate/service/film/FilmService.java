package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmService {

    Film create(Film film);
    Film update(Film film);
    List<Film> getAll();
    Film getById(String id);

}
