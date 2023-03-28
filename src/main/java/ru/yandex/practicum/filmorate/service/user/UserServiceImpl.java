package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static int count;
    private final UserStorage storage;

    @Override
    public User create(User user) {
        user.setId(++count);
        validateInstance(user);
        log.info("Создан пользователь: {}", user);
        return storage.save(user);
    }

    @Override
    public User update(User user) {
        validateInstance(user);
        getUserOrThrow(user.getId());
        storage.save(user);
        log.info("Пользователь обновлён: {}", user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return storage.findAll();
    }

    @Override
    public User getById(String stringId) {
        long id = validateId(stringId);
        return getUserOrThrow(id);
    }

    @Override
    public User addFriend(String stringId, String stringFriendId) {
        long userId = validateId(stringId);
        long friendId = validateId(stringFriendId);
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        user.addFriendId(friend.getId());
        return friend;
    }

    @Override
    public User deleteFriendById(String userStrId, String friendStrId) {
        long userId = validateId(userStrId);
        long friendId = validateId(friendStrId);
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        boolean friendFound = user.deleteFriendId(friend.getId());
        if (!friendFound) {
            throw new UserNotFoundException("Пользователь не является вашим другом");
        } else {
            return friend;
        }
    }

    @Override
    public List<User> getAllFriends(String id) {
        long userId = validateId(id);
        User user = getUserOrThrow(userId);
        return storage.findAllById(user.getFriends());
    }

    private static void validateInstance(User user) {
        if (user.getName() == null || user.getName()
                .isBlank()) {
            user.setName(user.getLogin());
        }
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

    private User getUserOrThrow(long id) {
        return storage.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Пользователь с id=%d не найден", id)));
    }

}
