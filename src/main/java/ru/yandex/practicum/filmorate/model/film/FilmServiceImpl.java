package ru.yandex.practicum.filmorate.model.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilmServiceImpl implements FilmService {

    private static final LocalDate FIRST_FILM = LocalDate.of(1895, 12, 28);
    private static int count;
    private final Map<Integer, Film> films;

    public FilmServiceImpl() {
        films = new HashMap<>();
    }

    @Override
    public Film addFilm(Film film) {
        validate(film);
        film.setId(++count);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            validate(film);
            films.put(film.getId(), film);
            return film;
        } else {
            throw new FilmNotFoundException("Фильма с таким id не найдено");
        }
    }

    @Override
    public List<Film> returnAllFilms() {
        return new ArrayList<>(films.values());
    }

    private static void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        } else if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        } else if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(FIRST_FILM)) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        } else if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

}
