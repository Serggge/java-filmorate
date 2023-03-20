package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private static final LocalDate FIRST_FILM = LocalDate.of(1895, 12, 28);
    private static int count;
    @Autowired
    private FilmRepository repository;

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
            throw new FilmNotFoundException("Фильм с указанным id не найден");
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
            throw new ValidationException("Дата релиза фильма раньше 28 декабря 1895 года");
        }
    }

}
