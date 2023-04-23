package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataUpdateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Repository("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users;

    public InMemoryUserStorage() {
        users = new HashMap<>();
    }

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(long id) {
        return users.containsKey(id) ? Optional.of(users.get(id)) : Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> findAllById(Iterable<Long> ids) {
        List<User> result = new ArrayList<>();
        for (Long id : ids) {
            if (users.containsKey(id)) {
                result.add(users.get(id));
            }
        }
        return result;
    }

    @Override
    public void deleteById(long id) {
        User user = users.remove(id);
        if (user == null) {
            throw new DataUpdateException("User not found: id=" + id);
        }
    }

    @Override
    public void deleteAllById(Iterable<Long> ids) {
        for (Long id : ids) {
            users.remove(id);
        }
    }

    @Override
    public void deleteAll(Iterable<User> users) {
        LinkedList<Long> ids = new LinkedList<>();
        for (User user : users) {
            ids.addLast(user.getId());
        }
        deleteAllById(ids);
    }
}
