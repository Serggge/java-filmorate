package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class GenreServiceImpl implements GenreService {

    private final GenreStorage storage;

    @Override
    public List<Genre> getAll() {
        log.debug("Запрошен список всех жанров");
        return storage.findAll();
    }

    @Override
    public Genre getById(long id) {
        log.debug("Запрошен жанр id=" + id);
        return storage.findById(id).orElseThrow(() -> new GenreNotFoundException(
                String.format("Жанр с id=%d не найден", id)
        ));
    }

}
