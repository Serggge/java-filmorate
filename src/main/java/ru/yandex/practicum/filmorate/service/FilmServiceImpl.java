package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static ru.yandex.practicum.filmorate.Constants.FIRST_FILM;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private static int count;
    private final FilmRepository repository;

    @Override
    public Film create(Film film) {
        film.setId(++count);
        validate(film);
        log.info("Добавлен фильм: {}", film);
        return repository.save(film);
    }

    @Override
    public Film update(Film film) {
        Optional<Film> existingFilm = repository.findById(film.getId());
        if (existingFilm.isPresent()) {
            validate(film);
            film = repository.save(film);
            log.info("Обновлён фильм: {}", film);
        } else {
            throw new FilmNotFoundException(
                    String.format("Фильм с id=%d не найден", film.getId())
            );
        }
        return film;
    }

    @Override
    public List<Film> getAll() {
        return repository.findAll();
    }

    private static void validate(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate.isBefore(FIRST_FILM)) {
            throw new ValidationException(
                    String.format("Дата релиза фильма раньше %s", FIRST_FILM)
            );
        }
    }

}
