package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataUpdateException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.DirectorsStorage;
import ru.yandex.practicum.filmorate.storage.dao.EventStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;
import static ru.yandex.practicum.filmorate.service.Validator.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.time.Instant;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final LikeStorage likeStorage;
    private final UserService userService;
    private final DirectorsStorage directorsStorage;
    private final EventStorage eventStorage;

    @Autowired
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                           FilmGenreStorage filmGenreStorage,
                           LikeStorage likeStorage,
                           UserService userService,
                           DirectorsStorage directorsStorage,
                           EventStorage eventStorage) {
        this.filmStorage = filmStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.likeStorage = likeStorage;
        this.userService = userService;
        this.directorsStorage = directorsStorage;
        this.eventStorage = eventStorage;
    }

    @Override
    public Film create(Film film) {
        film = validateFilm(film);
        if (film.getId() != 0) {
            throw new ValidationException("Недопустимый параметр ID при создании фильма");
        }
        film = filmStorage.save(film);
        if (!film.getGenres().isEmpty()) {
            filmGenreStorage.save(film);
        }
        if (!film.getDirectors().isEmpty()) {
            directorsStorage.save(film);
        }
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        film = validateFilm(film);
        checkFilmExistence(film.getId());
        filmStorage.update(film);
        filmGenreStorage.deleteByFilmId(film.getId());
        filmGenreStorage.save(film);
        directorsStorage.deleteByFilmId(film.getId());
        directorsStorage.save(film);
        film.getLikes().addAll(likeStorage.findUsersIdByFilmId(film.getId()));
        log.info("Обновлён фильм: {}", film);
        return film;
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
    public void setLike(long filmId, long userId) {
        checkFilmExistence(filmId);
        checkUserExistence(userId);
        Like like = new Like(filmId, userId);
        if (!likeStorage.isExist(like)) {
            likeStorage.save(like);
            log.info("Пользователь: id={} поставил лайк фильму: id={}", userId, filmId);
        }
        eventStorage.save(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .userId(userId)
                .entityId(filmId)
                .build());
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        checkFilmExistence(filmId);
        checkUserExistence(userId);
        Like like = new Like(filmId, userId);
        if (likeStorage.isExist(like)) {
            likeStorage.delete(like);
            log.info("Пользователь: id={} убрал лайк фильму: id={}", userId, filmId);
            eventStorage.save(Event.builder()
                    .timestamp(Instant.now().toEpochMilli())
                    .eventType(EventType.LIKE)
                    .operation(Operation.REMOVE)
                    .userId(userId)
                    .entityId(filmId)
                    .build());
        } else {
            throw new DataUpdateException("Пользователь ранее не оставлял лайк");
        }
    }

    @Override
    public List<Film> getPopular(Map<String, String> allParams) {
        log.debug("Запрошен список самых популярных фильмов");
        int count = allParams.containsKey("count") ? parseSafely(Integer::parseInt, allParams.get("count")) : 10;
        Set<Long> foundedIds = new HashSet<>();
        if (!allParams.containsKey("year") && !allParams.containsKey("genreId")) {
            foundedIds.addAll(filmStorage.findPopular(count));
        } else if (allParams.containsKey("year") && allParams.containsKey("genreId")) {
            int year = parseSafely(Integer::parseInt, allParams.get("year"));
            int genreId = parseSafely(Integer::parseInt, allParams.get("genreId"));
            foundedIds.addAll(filmStorage.findByYearAndGenre(year, genreId));
        } else if (allParams.containsKey("year")) {
            int year = parseSafely(Integer::parseInt, allParams.get("year"));
            foundedIds.addAll(filmStorage.findAllByYear(year));
        } else if (allParams.containsKey("genreId")) {
            int genreId = parseSafely(Integer::parseInt, allParams.get("genreId"));
            foundedIds.addAll(filmGenreStorage.findAllByGenre(genreId));
        }
        return filmStorage.findAllById(foundedIds)
                .stream()
                .sorted(Comparator.comparingInt(Film::getPopularity).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getRecommendedFilms(long userId) {
        checkUserExistence(userId);
        log.debug("Запрошен список рекомендованных фильмов для пользователя id={}", userId);
        return filmStorage.findAllById(likeStorage.suggestFilms(userId));
    }

    @Override
    public List<Film> searchByParams(String query, List<String> by) {
        log.debug("Поиск подстроки: {} / параметры поиска: [{}]", query, by);
        Set<Long> foundedIds = new HashSet<>();
        if (by.contains("title")) {
            foundedIds.addAll(filmStorage.findBySubString(query));
        }
        if (by.contains("director")) {
            foundedIds.addAll(directorsStorage.findBySubString(query));
        }
        List<Film> foundedFilms = filmStorage.findAllById(foundedIds);
        foundedFilms.sort(Comparator.comparingInt(Film::getPopularity).reversed());
        return foundedFilms;
    }

    @Override
    public List<Film> getCommonFilmPopular(long userId, long friendId) {
        checkUserExistence(userId);
        checkUserExistence(friendId);
        List<Film> commonFilms = filmStorage.findAllById(likeStorage.findCommonLikes(userId, friendId));
        commonFilms.sort(Comparator.comparingInt(Film::getPopularity).reversed());
        return commonFilms;
    }

    @Override
    public List<Film> getSortedFilms(int directorId, String sortBy) {
        checkDirectorExistence(directorId);
        List<Long> ids = directorsStorage.getSortedFilms(directorId);
        List<Film> films = filmStorage.findAllById(ids);
        if (sortBy.equals("year")) {
            films.sort(Comparator.comparing(Film::getReleaseDate));
        } else {
            films.sort(Comparator.comparingInt(Film::getPopularity));
        }
        return films;
    }

    @Override
    public void delete(long filmId) {
        log.info("Удаление фильма id={}", filmId);
        if (!filmStorage.existsById(filmId)) {
            throw new FilmNotFoundException(String.format("Фильм с id=%d не найден", filmId));
        }
        filmStorage.delete(filmId);
    }

    private Film getFilmOrThrow(long id) {
        return filmStorage.findById(id).orElseThrow(() ->
                new FilmNotFoundException(String.format("Фильм с id=%d не найден", id)));
    }

    private <T extends Number> T parseSafely(Function<String, T> parser, String source) {
        try {
            return parser.apply(source);
        } catch (Exception e) {
            throw new IllegalArgumentException("Некорректный параметр: " + source);
        }
    }

    private void checkUserExistence(long userId) {
        if (!userService.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }
    }

    private void checkFilmExistence(long filmId) {
        if (!filmStorage.existsById(filmId)) {
            throw new FilmNotFoundException(String.format("Фильм с id=%d не найден", filmId));
        }
    }

    private void checkDirectorExistence(int directorId) {
        if (!directorsStorage.existsById(directorId)) {
            throw new FilmNotFoundException(String.format("Режиссёр с id=%d не найден", directorId));
        }
    }

}
