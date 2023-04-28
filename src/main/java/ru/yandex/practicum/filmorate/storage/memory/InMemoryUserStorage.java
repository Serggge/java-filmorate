package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.*;
import java.util.stream.Collectors;

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
    public List<User> findAllById(Collection<Long> ids) {
        return ids.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(long id) {
        User user = users.remove(id);
        if (user == null) {
            throw new UserNotFoundException("User not found: id=" + id);
        }
    }

    @Override
    public void deleteAllById(Collection<Long> ids) {
        ids.stream().forEach(id -> {
            if (users.containsKey(id)) {
                users.remove(id);
            }
        });
    }

    @Override
    public boolean existsById(long id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteAll() {
        users.clear();
    }
}
