package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static int count;
    private final UserStorage storage;

    @Override
    public User create(User user) {
        user.setId(++count);
        validate(user);
        log.info("Создан пользователь: {}", user);
        return storage.save(user);
    }

    @Override
    public User update(User user) {
        Optional<User> existingUser = storage.findById(user.getId());
        if (existingUser.isPresent()) {
            validate(user);
            storage.save(user);
            log.info("Пользователь обновлён: {}", user);
        } else {
            throw new UserNotFoundException(String.format("Пользователь с id=%d не найден", user.getId()));
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        return storage.findAll();
    }

    private static void validate(User user) {
        if (user.getName() == null || user.getName()
                                          .isBlank()) {
            user.setName(user.getLogin());
        }
    }

    @Override
    public User getById(String stringId) {
        long id = validateId(stringId);
        return storage.findById(id)
                      .orElseThrow(
                              () -> new UserNotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

    @Override
    public User addFriend(String stringId, String stringFriendId) {
        long id = validateId(stringId);
        long friendId = validateId(stringFriendId);
        User user = storage.findById(id)
                           .orElseThrow(() -> new UserNotFoundException(
                                   String.format("Пользователь с id=%d не найден", id)));
        User friend = storage.findById(friendId)
                             .orElseThrow(() -> new UserNotFoundException(
                                     String.format("Друг с id=%d не найден", friendId)));
        user.addFriendId(friend.getId());
        return friend;
    }

    private static long validateId(String stringId) {
        try {
            final long longId = Long.parseLong(stringId);
            if (longId <= 0) {
                throw new IncorrectParameterException("id", "Идентификатор должен быть больше 0");
            }
            return longId;
        } catch (NumberFormatException e) {
            throw new IncorrectParameterException("id", "Идентификатор не числовой");
        }
    }

}
