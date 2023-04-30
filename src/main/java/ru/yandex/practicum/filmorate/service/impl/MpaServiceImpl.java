package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaRatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MpaServiceImpl implements MpaService {

    private final MpaStorage storage;

    public MpaServiceImpl(@Autowired MpaStorage mpaStorage) {
        this.storage = mpaStorage;
    }

    @Override
    public List<Mpa> getAll() {
        log.debug("Запрошен список всех рейтингов MPA");
        return new ArrayList<>(storage.findAll());
    }

    @Override
    public Mpa getById(long id) {
        log.debug("Запрошена информация о рейтинге MPA id=" + id);
        return storage.findById(id).orElseThrow(() -> new MpaRatingNotFoundException(
                String.format("Рейтинг MPA с id=%d не найден", id)
        ));
    }

}
