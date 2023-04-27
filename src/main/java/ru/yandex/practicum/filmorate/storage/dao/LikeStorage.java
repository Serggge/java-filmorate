package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Like;
import java.util.Collection;

public interface LikeStorage {

    Like save(Like like);

    Collection<Long> findAllById(long id);

    void deleteById(Like like);

    boolean isExist(Like like);

}
