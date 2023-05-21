package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorsService;
import ru.yandex.practicum.filmorate.storage.dao.DirectorsStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class DirectorsServiceImpl implements DirectorsService {

    private final DirectorsStorage storage;

    @Override
    public List<Director> getAllDirectors() {
        log.debug("Запрошен список всех режиссеров");
        return storage.getAllDirectors();
    }

    @Override
    public Director getDirectorById(int id) {
        log.debug("Запрошен режиссер " + id);
        return storage.getDirectorById(id);
    }

    @Override
    public Director createDirector(Director director) {
        log.debug("Запрошено создание режиссера" + director.getId());
        return storage.createDirector(director);
    }

    @Override
    public Director updateDirector(Director director) {
        log.debug("Запрошено обновление режиссера" + director.getId());
        return storage.updateDirector(director);
    }

    @Override
    public void deleteDirector(int id) {
        log.debug("Запрошено удаление режиссера " + id);
        storage.deleteDirector(id);
    }

}
