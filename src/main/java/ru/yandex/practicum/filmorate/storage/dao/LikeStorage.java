package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Like;
import java.util.Collection;
import java.util.List;

public interface LikeStorage {

    Like save(Like like);

    Collection<Long> findAllById(long id);

    void deleteById(Like like);

    List<Long> getPopular(int size);

    boolean isExist(Like like);

}
