package ru.yandex.practicum.filmorate.storage;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
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

}
