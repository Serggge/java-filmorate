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
import ru.yandex.practicum.filmorate.storage.dao.EventStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;

import static ru.yandex.practicum.filmorate.service.Validator.*;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final LikeStorage likeStorage;
    private final UserService userService;
    private final EventStorage eventStorage;

    @Autowired
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                           FilmGenreStorage filmGenreStorage,
                           LikeStorage likeStorage,
                           UserService userService,
                           EventStorage eventStorage) {
        this.filmStorage = filmStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.likeStorage = likeStorage;
        this.userService = userService;
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
            film = filmGenreStorage.save(film);
        }
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        film = validateFilm(film);
        if (film.getId() == 0) {
            throw new ValidationException("Для обновления требуется указать ID фильма");
        } else if (filmStorage.existsById(film.getId())) {
            film = filmStorage.update(film);
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
        return constructFilmList(filmStorage.findAllIds());
        List<Film> films = filmStorage.findAll();
        List<Long> filmsIds = films
                .stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        Map<Long, Set<Genre>> filmsGenres = filmGenreStorage.findAll(filmsIds);
        Map<Long, Set<Long>> filmsLikes = likeStorage.findAll(filmsIds);
        for (Film film : films) {
            if (filmsGenres.containsKey(film.getId())) {
                film.getGenres().addAll(filmsGenres.get(film.getId()));
            }
            if (filmsLikes.containsKey(film.getId())) {
                film.getLikes().addAll(filmsLikes.get(film.getId()));
            }
        }
        Collections.sort(films);
        return films;
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
        eventStorage.save(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .userId(userId)
                .entityId(filmId)
                .build());
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
        eventStorage.save(Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .userId(userId)
                .entityId(filmId)
                .build());
        return film;
    }

    @Override
    public List<Film> getPopular(Map<String, String> allParams) {
        log.debug("Запрошен список самых популярных фильмов");
        List<Film> films = filmStorage.findAll();
        List<Long> filmsIds = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        Map<Long, Set<Genre>> filmsGenres = filmGenreStorage.findAll(filmsIds);
        Map<Long, Set<Long>> filmsLikes = likeStorage.findAll(filmsIds);
        for (Film film : films) {
            if (filmsGenres.containsKey(film.getId())) {
                film.getGenres().addAll(filmsGenres.get(film.getId()));
            }
            if (filmsLikes.containsKey(film.getId())) {
                film.getLikes().addAll(filmsLikes.get(film.getId()));
            }
        }
        return films.stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
        int count = allParams.containsKey("count") ? safelyParse(Integer::parseInt, allParams.get("count")) : 10;
        Set<Long> foundedIds = new HashSet<>();
        if (allParams.containsKey("year")) {
            int year = safelyParse(Integer::parseInt, allParams.get("year"));
            foundedIds.addAll(filmStorage.findAllByYear(year));
        }
        if (allParams.containsKey("genreId")) {
            int genreId = safelyParse(Integer::parseInt, allParams.get("genreId"));
            foundedIds.addAll(filmStorage.findAllByGenre(genreId));
        }
        if (foundedIds.size() < count) {
            foundedIds.addAll(likeStorage.findPopular(count - foundedIds.size()));
        }

        List<Film> foundedFilms = foundedIds.isEmpty()
                ? constructFilmList(filmStorage.findAllIds())
                : constructFilmList(foundedIds);
        return foundedFilms.stream()
                .sorted(Comparator.comparingInt(Film::popularity).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getRecommendedFilms(long userId) {
        if (!userService.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }
        log.debug("Запрошен список рекомендованных фильмов для пользователя id={}", userId);
        return constructFilmList(likeStorage.suggestFilms(userId));
    }

    @Override
    public List<Film> searchByParams(String query, List<String> by) {
        //TODO AFTER IMPLEMENTATION DIRECTORS FEATURE
        log.debug("Поиск подстроки: {} / параметры поиска: [{}]", query, by);
        List<Film> foundedFilms = constructFilmList(filmStorage.findBySubString(query));
        foundedFilms.sort(Comparator.comparingInt(Film::popularity).reversed());
        return foundedFilms;
    }

    @Override
    public List<Film> searchByParams(String query, List<String> by) {
        Set<Film> foundFilms = new HashSet<>();
        if (by == null || by.isEmpty() || by.contains("title")) {
            foundFilms.addAll(filmStorage.findBySubString(query));
        }
        if (by != null && by.contains("director")) {
            //foundFilms.addAll();
        }
        Set<Long> filmIds = foundFilms
                .stream()
                .map(Film::getId)
                .collect(Collectors.toSet());
        Map<Long, Set<Genre>> filmGenres = filmGenreStorage.findAll(filmIds);
        for (Film film : foundFilms) {
            if (filmGenres.containsKey(film.getId())) {
                film.getGenres().addAll(filmGenres.get(film.getId()));
            }
        }
        return foundFilms
                .stream()
                .sorted(Comparator.comparingInt(Film::popularity).reversed())
                .collect(Collectors.toList());
    }

    private Film getFilmOrThrow(long id) {
        Film saved = filmStorage.findById(id)
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id=%d не найден", id)));
        saved.getGenres().addAll(filmGenreStorage.findGenresByFilmId(id));
        return saved;
    }

    @Override
    public void delete(long filmId) {
        log.info("Удаление фильма id={}", filmId);
        if (!filmStorage.existsById(filmId)) {
            throw new FilmNotFoundException(String.format("Фильм с id=%d не найден", filmId));
        }
        filmStorage.delete(filmId);
    }

    private List<Film> constructFilmList(Collection<Long> filmsIds) {
        List<Film> films = filmStorage.findAllById(filmsIds);
        Map<Long, Set<Genre>> filmsGenres = filmGenreStorage.findAll(filmsIds);
        Map<Long, Set<Long>> filmsLikes = likeStorage.findAll(filmsIds);
        for (Film film : films) {
            if (filmsGenres.containsKey(film.getId())) {
                film.getGenres().addAll(filmsGenres.get(film.getId()));
            }
            if (filmsLikes.containsKey(film.getId())) {
                film.getLikes().addAll(filmsLikes.get(film.getId()));
            }
        }
        return films;
    }

    private <T extends Number> T safelyParse(Function<String, T> parser, String source) {
        try {
            return parser.apply(source);
        } catch (Exception e) {
            throw new IllegalArgumentException("Некорректный параметр: " + source);
        }
    }

}
