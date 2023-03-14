package ru.yandex.practicum.filmorate.model.user;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final Map<Integer, User> users;
    private static int count;

    public UserService() {
        users = new HashMap<>();
    }

    public User add(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(++count);
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        if (user.getId() != 0 && users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        } else {
            throw new ValidationException("Пользователя с таким id не существует");
        }
    }

    public List<User> list() {
        return new ArrayList<>(users.values());
    }
}
