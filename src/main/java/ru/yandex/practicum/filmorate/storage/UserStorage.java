package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User save(User user);

    Optional<User> findById(long id);

    List<User> findAll();

    List<User> findAllById(Iterable<Long> ids);

    void deleteById(long id);

    void deleteAllById(Iterable<Long> ids);

    void deleteAll(Iterable<User> users);

    boolean existsById(long id);

}
