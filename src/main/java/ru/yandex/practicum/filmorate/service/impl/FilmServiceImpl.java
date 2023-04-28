package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataUpdateException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;
import static ru.yandex.practicum.filmorate.service.Validator.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final LikeStorage likeStorage;
    private final UserService userService;

    @Autowired
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                           FilmGenreStorage filmGenreStorage,
                           LikeStorage likeStorage,
                           UserService userService) {
        this.filmStorage = filmStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.likeStorage = likeStorage;
        this.userService = userService;
    }

    @Override
    public Film create(Film film) {
        validateFilm(film);
        if (film.getId() != 0) {
            throw new ValidationException("Недопустимый параметр ID при создании фильма");
        }
        film = filmStorage.save(film);
        if (!film.getGenres().isEmpty()) {
            film = filmGenreStorage.save(film);
        }
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        validateFilm(film);
        if (film.getId() == 0) {
            throw new ValidationException("Для обновления требуется указать ID фильма");
        } else if (filmStorage.existsById(film.getId())) {
            film = filmStorage.save(film);
            filmGenreStorage.deleteByFilmId(film.getId());
            filmGenreStorage.save(film);
            film.getLikes().addAll(likeStorage.findUsersIdByFilmId(film.getId()));
            log.info("Обновлён фильм: {}", film);
            return film;
        } else {
            throw new FilmNotFoundException(String.format("Фильм: id=%d не найден", film.getId()));
        }
    }

    @Override
    public List<Film> getAll() {
        log.debug("Запрос списка всех фильмов");
        return filmStorage.findAll()
                .stream()
                .peek(film -> film.getGenres().addAll(filmGenreStorage.findGenresByFilmId(film.getId())))
                .peek(film -> film.getLikes().addAll(likeStorage.findUsersIdByFilmId(film.getId())))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public Film getById(long id) {
        log.debug("Запрошен фильм: id={}", id);
        Film film = getFilmOrThrow(id);
        film.getGenres().addAll(filmGenreStorage.findGenresByFilmId(id));
        film.getLikes().addAll(likeStorage.findUsersIdByFilmId(id));
        return film;
    }

    @Override
    public Film setLike(long filmId, long userId) {
        Film film = getFilmOrThrow(filmId);
        User user = userService.getById(userId);
        Like like = new Like(filmId, userId);
        if (!likeStorage.isExist(like)) {
            likeStorage.save(like);
            log.info("Пользователь: id={} поставил лайк фильму: id={}", userId, filmId);
            film.getGenres().addAll(filmGenreStorage.findGenresByFilmId(filmId));
            film.getLikes().addAll(likeStorage.findUsersIdByFilmId(filmId));
        } else {
            throw new DataUpdateException(
                    String.format("Пользователь id=%d уже оставлял лайк фильму id=%d", userId, filmId));
        }
        return film;
    }

    @Override
    public Film deleteLike(long filmId, long userId) {
        Film film = getFilmOrThrow(filmId);
        User user = userService.getById(userId);
        Like like = new Like(filmId, userId);
        if (likeStorage.isExist(like)) {
            likeStorage.delete(like);
            log.info("Пользователь: id={} убрал лайк фильму: id={}", userId, filmId);
            film.getGenres().addAll(filmGenreStorage.findGenresByFilmId(filmId));
            film.getLikes().addAll(likeStorage.findUsersIdByFilmId(filmId));
        } else {
            throw new DataUpdateException("Пользователь ранее не оставлял лайк");
        }
        return film;
    }

    @Override
    public List<Film> getPopular(int count) {
        log.debug("Запрошен список самых популярных фильмов");
        return filmStorage.findAll()
                .stream()
                .peek(film -> film.getGenres().addAll(filmGenreStorage.findGenresByFilmId(film.getId())))
                .peek(film -> film.getLikes().addAll(likeStorage.findUsersIdByFilmId(film.getId())))
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilmOrThrow(long id) {
        Film saved = filmStorage.findById(id)
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id=%d не найден", id)));
        for (Genre genre : filmGenreStorage.findGenresByFilmId(id)) {
            saved.addGenre(genre);
        }
        return saved;
    }

}
