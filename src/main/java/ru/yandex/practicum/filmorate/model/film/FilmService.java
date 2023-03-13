package ru.yandex.practicum.filmorate.model.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilmService {

    private static final LocalDate FIRST_FILM = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films;

    public FilmService() {
        films = new HashMap<>();
    }

    public Film add(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM)) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        } else {
            films.put(film.getId(), film);
            return film;
        }
    }

    public List<Film> list() {
        return new ArrayList<>(films.values());
    }

}
