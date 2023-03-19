package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmService {

    Film addNewFilm(Film film);
    Film updateIncomingFilm(Film film);
    List<Film> returnAllFilms();

}
