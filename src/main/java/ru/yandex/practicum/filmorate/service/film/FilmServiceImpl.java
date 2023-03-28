package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.Constants.FIRST_FILM;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private static int count;
    private final FilmStorage storage;

    @Override
    public Film create(Film film) {
        film.setId(++count);
        validateInstance(film);
        log.info("Добавлен фильм: {}", film);
        return storage.save(film);
    }

    @Override
    public Film update(Film film) {
        Optional<Film> existingFilm = storage.findById(film.getId());
        if (existingFilm.isPresent()) {
            validateInstance(film);
            film = storage.save(film);
            log.info("Обновлён фильм: {}", film);
        } else {
            throw new FilmNotFoundException(String.format("Фильм с id=%d не найден", film.getId()));
        }
        return film;
    }

    @Override
    public List<Film> getAll() {
        return storage.findAll();
    }

    @Override
    public Film getById(String stringId) {
        try {
            final long longId = Long.parseLong(stringId);
            return storage.findById(longId).orElseThrow(() -> new FilmNotFoundException(
                    String.format("Фильм с id=%d не найден", longId))
                    );
        } catch (NumberFormatException e) {
            throw new IncorrectParameterException("id", "Идентификатор не числовой");
        }
    }

    private static void validateInstance(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate.isBefore(FIRST_FILM)) {
            throw new ValidationException(String.format("Дата релиза фильма раньше %s", FIRST_FILM));
        }
    }

}
