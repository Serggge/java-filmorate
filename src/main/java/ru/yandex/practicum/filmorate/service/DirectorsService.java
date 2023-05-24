package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorsService {

    List<Director> getAll();

    Director getById(int id);

    Director create(Director director);

    Director update(Director director);

    void delete(int id);

}
