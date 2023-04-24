package ru.yandex.practicum.filmorate.service.impl;

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
import ru.yandex.practicum.filmorate.storage.dao.FilmGenresStorage;
import static ru.yandex.practicum.filmorate.service.Validator.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final FilmGenresStorage filmGenresStorage;
    private final UserService userService;

    @Autowired
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage, FilmGenresStorage filmGenresStorage,
                           UserService userService) {
        this.filmStorage = filmStorage;
        this.filmGenresStorage = filmGenresStorage;
        this.userService = userService;
    }

    @Override
    public Film create(Film film) {
        validateFilm(film);
        Film saved = filmStorage.save(film);
        if (!film.getGenres().isEmpty()) {
            upsertGenres(saved);
        }
        log.info("Добавлен фильм: {}", saved);
        return saved;
    }

    @Override
    public Film update(Film film) {
        validateFilm(film);
        if (filmStorage.findById(film.getId()).isPresent()) {
            Film saved = filmStorage.save(film);
            log.info("Обновлён фильм: {}", saved);
            return saved;
        } else {
            throw new FilmNotFoundException(String.format("Фильм: id=%d не найден", film.getId()));
        }
    }

    @Override
    public List<Film> getAll() {
        log.debug("Запрос списка всех фильмов");
        return filmStorage.findAll();
    }

    @Override
    public Film getById(long id) {
        log.debug("Запрошен фильм: id={}", id);
        return getFilmOrThrow(id);
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
                      .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                      .limit(count)
                      .collect(Collectors.toList());
    }

    private Film upsertGenres(Film film) {
        filmGenresStorage.deleteById(film.getId());
        return filmGenresStorage.save(film);
    }

    private Film getFilmOrThrow(long id) {
        Film saved = filmStorage.findById(id)
                      .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id=%d не найден", id)));
        for (Genre genre : filmGenresStorage.findAllById(id)) {
            saved.addGenre(genre);
        }
        return saved;
    }

}
