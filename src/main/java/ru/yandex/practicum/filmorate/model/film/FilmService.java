package ru.yandex.practicum.filmorate.model.film;

import java.util.List;

public interface FilmService {

    Film addFilm(Film film);
    Film updateFilm(Film film);
    List<Film> returnAllFilms();

}
