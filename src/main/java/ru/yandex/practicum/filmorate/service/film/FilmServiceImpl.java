package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static int count;
    private final FilmStorage storage;
    private UserService userService;

    @Autowired
    public FilmServiceImpl(FilmStorage storage) {
        this.storage = storage;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Film create(Film film) {
        validateFilm(film);
        film.setId(++count);
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
            throw new FilmNotFoundException(String.format("Фильм с id=%d не найден", film.getId()));
        }
    }

    @Override
    public List<Film> getAll() {
        return storage.findAll();
    }

    @Override
    public Film getById(String id) {
        long filmId = validateId(id);
        return getFilmOrThrow(filmId);
    }

    @Override
    public Film setLike(String id, String userId) {
        long filmId = validateId(id);
        Film film = getFilmOrThrow(filmId);
        User user = userService.getById(userId);
        film.addLike(user.getId());
        log.info("Пользователь id={} поставил лайк фильму id={}", userId, filmId);
        return film;
    }

    @Override
    public Film deleteLike(String id, String userId) {
        long filmId = validateId(id);
        Film film = getFilmOrThrow(filmId);
        User user = userService.getById(userId);
        boolean isSuccess = film.removeLike(user.getId());
        if (!isSuccess) {
            throw new DataUpdateException("Пользователь ранее не оставлял лайк");
        }
        log.info("Пользователь id={} убрал лайк фильму id={}", userId, filmId);
        return film;
    }

    @Override
    public List<Film> getPopular(String count) {
        long size = validateId(count);
        return storage.findAll()
                      .stream()
                      .sorted((film1, film2) -> film2.getLikes()
                                                     .size() - film1.getLikes()
                                                                    .size())
                      .limit(size)
                      .collect(Collectors.toList());
    }

    private Film getFilmOrThrow(long id) {
        return storage.findById(id)
                      .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id=%d не найден", id)));
    }

}
