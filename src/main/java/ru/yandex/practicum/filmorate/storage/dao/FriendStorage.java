package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Friendship;
import java.util.List;
import java.util.Optional;

public interface FriendStorage {

    Friendship save(Friendship friendship);

    Optional<Friendship> find(Friendship friendship);

    List<Long> findFriendsIdByUserId(long id);

    void cancel(Friendship friendship);

    boolean isExist(Friendship friendship);

    boolean isConfirmed(Friendship friendship);

    boolean confirm(Friendship friendship);

    void deleteAll();

}
