package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataUpdateException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import static ru.yandex.practicum.filmorate.service.Validator.*;
import java.util.List;
import java.util.Optional;
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
        Optional<Film> existingFilm = storage.findById(film.getId());
        if (existingFilm.isPresent()) {
            validateFilm(film);
            film = storage.save(film);
            log.info("Обновлён фильм: {}", film);
        } else {
            throw new FilmNotFoundException(String.format("Фильм с id=%d не найден", film.getId()));
        }
        return film;
    }

    @Override
    public List<Film> getAll() {
        return storage.findAll();
    }

    @Override
    public Film getById(String stringId) {
        try {
            final long longId = Long.parseLong(stringId);
            return storage.findById(longId)
                          .orElseThrow(
                                  () -> new FilmNotFoundException(String.format("Фильм с id=%d не найден", longId)));
        } catch (NumberFormatException e) {
            throw new IncorrectParameterException("id", "Идентификатор не числовой");
        }
    }

    @Override
    public Film setLike(String filmStrId, String userId) {
        //long filmId = validateId(filmStrId);
        Film film = getFilmOrThrow(Long.parseLong(filmStrId));
        User user = userService.getById(userId);
        film.addLike(user.getId());
        return film;
    }

    @Override
    public Film deleteLike(String filmStrId, String userId) {
        //long filmId = validateId(filmStrId);
        Film film = getFilmOrThrow(Long.parseLong(filmStrId));
        User user = userService.getById(userId);
        boolean isSuccess = film.removeLike(user.getId());
        if (!isSuccess) {
            throw new DataUpdateException("Пользователь ранее не оставлял лайк");
        }
        return film;
    }

    @Override
    public List<Film> getPopular(String count) {
        //long size = validateId(count);
        return storage.findAll()
                      .stream()
                      .sorted((film1, film2) -> film2.getLikes()
                                                     .size() - film1.getLikes()
                                                                    .size())
                      .limit(Long.parseLong(count))
                      .collect(Collectors.toList());
    }

    private Film getFilmOrThrow(long id) {
        return storage.findById(id)
                      .orElseThrow(() -> new UserNotFoundException(String.format("Фильм с id=%d не найден", id)));
    }

}
