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
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;

import static ru.yandex.practicum.filmorate.service.Validator.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final LikeStorage likeStorage;
    private final UserService userService;
    private final DirectorsStorage directorsStorage;


    @Autowired
    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                           FilmGenreStorage filmGenreStorage,
                           LikeStorage likeStorage,
                           UserService userService,
                           DirectorsStorage directorsStorage) {
        this.filmStorage = filmStorage;
        this.filmGenreStorage = filmGenreStorage;
        this.likeStorage = likeStorage;
        this.userService = userService;
        this.directorsStorage = directorsStorage;
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
        if (!film.getDirectors().isEmpty()) {
            film = directorsStorage.save(film);
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
            film = filmStorage.save(film);
            filmGenreStorage.deleteByFilmId(film.getId());
            filmGenreStorage.save(film);
            directorsStorage.deleteByFilmId(film.getId());
            directorsStorage.save(film);
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
        List<Film> allFilms = filmStorage.findAll();
        List<Long> filmsIds = allFilms
                .stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        Map<Long, Set<Genre>> filmsGenres = filmGenreStorage.findAll(filmsIds);
        Map<Long, Set<Long>> filmsLikes = likeStorage.findAll(filmsIds);
        Map<Long, Set<Director>> filmsDirectors = directorsStorage.findAll(filmsIds);
        for (Film film : allFilms) {
            if (filmsGenres.containsKey(film.getId())) {
                film.getGenres().addAll(filmsGenres.get(film.getId()));
            }
            if (filmsLikes.containsKey(film.getId())) {
                film.getLikes().addAll(filmsLikes.get(film.getId()));
            }
            if (filmsDirectors.containsKey(film.getId())) {
                film.getDirectors().addAll(filmsDirectors.get(film.getId()));
            }
        }
        return allFilms;
    }

    @Override
    public Film getById(long id) {
        log.debug("Запрошен фильм: id={}", id);
        Film film = getFilmOrThrow(id);
        film.getGenres().addAll(filmGenreStorage.findGenresByFilmId(id));
        film.getLikes().addAll(likeStorage.findUsersIdByFilmId(id));
        film.getDirectors().addAll(directorsStorage.findDirectorsByFilmId(id));
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

    @Override
    public List<Film> getCommonFilmPopular(long userId, long friendId) {
        log.debug("Запрошен список общий список фильмов с другом, отсортированный по популярности");
        if (!userService.existsById(userId)) {
            throw new UserNotFoundException(String.format("Пользователь с id=%s", userId));
        } else if (!userService.existsById(friendId)) {
            throw new UserNotFoundException(String.format("Пользователь с id=%s", friendId));
        }
        return filmStorage.findAllById(likeStorage.findCommonLikes(userId, friendId))
                .stream()
                .sorted(Comparator.comparingInt(Film::popularity).reversed())
                .collect(Collectors.toList());
    }

    public List<Film> getSortedFilms(int directorId, String sortBy) {
        directorsStorage.getById(directorId);
        List<Long> ids = directorsStorage.getSortedFilms(directorId);
        List<Film> films = filmStorage.findAllById(ids);
        Map<Long, Set<Genre>> filmsGenres = filmGenreStorage.findAll(ids);
        Map<Long, Set<Long>> filmsLikes = likeStorage.findAll(ids);
        Map<Long, Set<Director>> filmsDirectors = directorsStorage.findAll(ids);
        for (Film film : films) {
            if (filmsGenres.containsKey(film.getId())) {
                film.getGenres().addAll(filmsGenres.get(film.getId()));
            }
            if (filmsLikes.containsKey(film.getId())) {
                film.getLikes().addAll(filmsLikes.get(film.getId()));
            }
            if (filmsDirectors.containsKey(film.getId())) {
                film.getDirectors().addAll(filmsDirectors.get(film.getId()));
            }
        }
        if (sortBy.equals("year")) {
            films.sort(Comparator.comparing(Film::getReleaseDate));
        } else {
            films.sort(Comparator.comparingInt(Film::popularity));
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

}
