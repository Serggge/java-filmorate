package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FilmServiceImpl implements FilmService {

    private static int count;
    @Autowired
    private FilmRepository repository;

    @Override
    public Film addNewFilm(Film film) {
        film.setId(nextId());
        log.info("Добавлен фильм: {}", film);
        return repository.save(film);
    }

    @Override
    public Film updateIncomingFilm(Film film) {
        Optional<Film> existingFilm = repository.findById(film.getId());
        if (existingFilm.isPresent()) {
            film = repository.save(film);
            log.info("Обновлён фильм: {}", film);
        } else {
            throw new FilmNotFoundException("Фильм с указанным id не найден");
        }
        return film;
    }

    @Override
    public List<Film> returnAllFilms() {
        return repository.findAll();
    }

    private int nextId() {
        return ++count;
    }

}
