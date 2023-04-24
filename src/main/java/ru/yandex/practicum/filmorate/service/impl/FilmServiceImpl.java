package ru.yandex.practicum.filmorate.service.impl;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataUpdateException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;

import static ru.yandex.practicum.filmorate.service.Validator.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final UserService userService;

    @Autowired
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                           FilmGenreStorage filmGenreStorage,
                           UserService userService) {
        this.filmStorage = filmStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.userService = userService;
    }

    @Override
    public Film create(Film film) {
        validateFilm(film);
        Film saved = filmStorage.save(film);
        if (!saved.getGenres().isEmpty()) {
            saved = filmGenreStorage.save(saved);
        }
        log.info("Добавлен фильм: {}", saved);
        return saved;
    }

    @Override
    public Film update(Film film) {
        validateFilm(film);
        if (filmStorage.existsById(film.getId())) {
            Film saved = filmStorage.save(film);
            saved = fillGenres(saved);
            log.info("Обновлён фильм: {}", saved);
            return saved;
        } else {
            throw new FilmNotFoundException(String.format("Фильм: id=%d не найден", film.getId()));
        }
    }

    @Override
    public List<Film> getAll() {
        log.debug("Запрос списка всех фильмов");
        return filmStorage.findAll()
                .stream()
                .peek(film -> film.getGenres().addAll(filmGenreStorage.findAllById(film.getId())))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public Film getById(long id) {
        log.debug("Запрошен фильм: id={}", id);
        Film saved = getFilmOrThrow(id);
        saved.getGenres().addAll(filmGenreStorage.findAllById(id));
        return saved;
    }

    @Override
    public Film setLike(long filmId, long userId) {
        Film film = getFilmOrThrow(filmId);
        User user = userService.getById(userId);
        film.addLike(user.getId());
        log.info("Пользователь: id={} поставил лайк фильму: id={}", userId, filmId);
        return film;
    }

    @Override
    public Film deleteLike(long filmId, long userId) {
        Film film = getFilmOrThrow(filmId);
        User user = userService.getById(userId);
        boolean isSuccess = film.removeLike(user.getId());
        if (!isSuccess) {
            throw new DataUpdateException("Пользователь ранее не оставлял лайк");
        }
        log.info("Пользователь: id={} убрал лайк фильму: id={}", userId, filmId);
        return film;
    }

    @Override
    public List<Film> getPopular(int count) {
        log.debug("Запрошен список самых популярных фильмов");
        return filmStorage.findAll()
                .stream()
                .sorted(Comparator.comparingLong(film -> film.getLikes().size()))
                .limit(count)
                .peek(film -> film.getGenres().addAll(filmGenreStorage.findAllById(film.getId())))
                .collect(Collectors.toList());
    }

    private Film fillGenres(Film film) {
        filmGenreStorage.deleteById(film.getId());
        return filmGenreStorage.save(film);
    }

    private Film getFilmOrThrow(long id) {
        Film saved = filmStorage.findById(id)
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id=%d не найден", id)));
        for (Genre genre : filmGenreStorage.findAllById(id)) {
            saved.addGenre(genre);
        }
        return saved;
    }

}
