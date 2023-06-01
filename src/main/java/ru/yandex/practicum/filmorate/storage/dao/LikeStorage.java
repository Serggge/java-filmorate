package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Like;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LikeStorage {

    boolean save(Like like);

    List<Long> findUsersIdByFilmId(long id);

    Map<Long, Set<Long>> findAll(Collection<Long> ids);

    boolean delete(Like like);

    boolean isExist(Like like);

    void deleteAll();

    List<Long> findCommonLikes(long userId, long friendId);

    List<Long> findPopular(int count);

    List<Long> suggestFilms(long userId);

}
