package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Event;
import java.util.List;

public interface EventStorage {

    void save(Event event);

    List<Event> find(long userId);

}
