package ru.yandex.practicum.filmorate.model.user;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private static int count;
    private final Map<Integer, User> users;

    public UserServiceImpl() {
        users = new HashMap<>();
    }

    @Override
    public User addUser(User user) {
        validate(user);
        user.setId(++count);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() != 0 && users.containsKey(user.getId())) {
            validate(user);
            users.put(user.getId(), user);
            return user;
        } else {
            throw new UserNotFoundException("Пользователя с таким id не существует");
        }
    }

    @Override
    public List<User> returnAllUsers() {
        return new ArrayList<>(users.values());
    }

    private static void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Электронная почта не может быть пустой");
        } else if (!user.getEmail().matches("^[A-Za-z]\\w*@\\w+\\.(ru|com|net|org)$")) {
            throw new ValidationException("Электронная почта должна содержать символ: '@'");
        } else if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        } else if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        } else if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
