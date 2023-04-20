package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataUpdateException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import static ru.yandex.practicum.filmorate.service.Validator.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private final FilmStorage storage;
    private final UserService userService;

    public FilmServiceImpl(@Qualifier("filmDbStorage") FilmStorage storage, UserService userService) {
        this.storage = storage;
        this.userService = userService;
    }

    @Override
    public Film create(Film film) {
        validateFilm(film);
        log.info("Добавлен фильм: {}", film);
        return storage.save(film);
    }

    @Override
    public Film update(Film film) {
        validateFilm(film);
        if (storage.findAllId().contains(film.getId())) {
            log.info("Обновлён фильм: {}", film);
            return storage.save(film);
        } else {
            throw new FilmNotFoundException(String.format("Фильм: id=%d не найден", film.getId()));
        }
    }

    @Override
    public List<Film> getAll() {
        log.debug("Запрос списка всех фильмов");
        return storage.findAll();
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
        return storage.findAll()
                      .stream()
                      .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                      .limit(count)
                      .collect(Collectors.toList());
    }

    private Film getFilmOrThrow(long id) {
        return storage.findById(id)
                      .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id=%d не найден", id)));
    }

}
