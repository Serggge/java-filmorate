package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Friendship;
import java.util.Collection;
import java.util.Optional;

public interface FriendStorage {

    Friendship save(Friendship friendship);

    Optional<Friendship> findById(Friendship friendship);

    Collection<Long> findAllById(long id);

    void deleteById(Friendship friendship);

    boolean isExist(Friendship friendship);

    boolean isConfirmed(Friendship friendship);

    boolean confirm(Friendship friendship);

}
