package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorage;
import java.util.List;

@Service
@Slf4j
public class GenreServiceImpl implements GenreService {

    private final GenreStorage storage;

    @Autowired
    public GenreServiceImpl(GenreStorage storage) {
        this.storage = storage;
    }

    @Override
    public Genre create(Genre genre) {
        Genre saved = storage.save(genre);
        log.info("Добавлен новый жанр: {}", saved);
        return saved;
    }

    @Override
    public Genre update(Genre genre) {
        Genre saved = storage.save(genre);
        log.info("Обновлён жанр: {}", saved);
        return saved;
    }

    @Override
    public List<Genre> getAll() {
        log.debug("Запрошен список всех жанров");
        return storage.findAll();
    }

    @Override
    public Genre getById(long id) {
        log.debug("Запрошен Жанр id=" + id);
        return storage.findById(id).orElseThrow(() -> new GenreNotFoundException(
                String.format("Жанр с id=%d не найден", id)
        ));
    }

}