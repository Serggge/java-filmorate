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
    public List<Director> getAll() {
        log.debug("Запрошен список всех режиссеров");
        return storage.getAll();
    }

    @Override
    public Director getById(int id) {
        log.debug("Запрошен режиссер " + id);
        return storage.getById(id);
    }

    @Override
    public Director create(Director director) {
        log.debug("Запрошено создание режиссера" + director.getId());
        return storage.create(director);
    }

    @Override
    public Director update(Director director) {
        log.debug("Запрошено обновление режиссера" + director.getId());
        return storage.update(director);
    }

    @Override
    public void delete(int id) {
        log.debug("Запрошено удаление режиссера " + id);
        storage.delete(id);
    }

}
