package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);
    Optional<User> findById(long id);
    List<User> findAll();

}
