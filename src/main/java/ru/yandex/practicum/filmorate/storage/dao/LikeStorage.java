package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Like;
import java.util.List;

public interface LikeStorage {

    Like save(Like like);

    List<Long> findUsersIdByFilmId(long id);

    void delete(Like like);

    boolean isExist(Like like);

    void deleteAll();

}
