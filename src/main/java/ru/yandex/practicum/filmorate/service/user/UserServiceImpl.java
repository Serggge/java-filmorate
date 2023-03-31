package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataUpdateException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import static ru.yandex.practicum.filmorate.service.Validator.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static long count;
    private final UserStorage storage;

    @Override
    public User create(User user) {
        validateUser(user);
        user.setId(++count);
        log.info("Создан пользователь: {}", user);
        return storage.save(user);
    }

    @Override
    public User update(User user) {
        validateUser(user);
        if (storage.findAllId().contains(user.getId())) {
            log.info("Пользователь обновлён: {}", user);
            return storage.save(user);
        } else {
            throw new UserNotFoundException(String.format("Пользователь: id=%d не найден", user.getId()));
        }
    }

    @Override
    public List<User> getAll() {
        log.debug("Запрошен список всех пользователей");
        return storage.findAll();
    }

    @Override
    public User getById(long id) {
        log.debug("Запрошен пользователь: id={}", id);
        return getUserOrThrow(id);
    }

    @Override
    public User addFriend(long userId, long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        if (user.getFriends().contains(friendId)) {
            throw new DataUpdateException("Пользователи уже являются друзьями");
        } else {
            user.addFriendId(friendId);
            friend.addFriendId(userId);
            log.info("Пользователь: id={} добавил в друзья пользователя: id={}", userId, friendId);
            return friend;
        }
    }

    @Override
    public User deleteFriendById(long userId, long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        boolean friendFound = user.deleteFriendId(friendId);
        if (!friendFound) {
            throw new UserNotFoundException("Пользователь не является вашим другом");
        } else {
            friend.deleteFriendId(userId);
            log.info("Пользователь: id={} удалил из друзей пользователя: id={}", userId, friendId);
            return friend;
        }
    }

    @Override
    public List<User> getAllFriends(long userId) {
        log.debug("Запрос списка друзей для пользователя: id={}", userId);
        User user = getUserOrThrow(userId);
        return storage.findAllById(user.getFriends());
    }

    @Override
    public List<User> getMutualFriends(long id, long otherId) {
        log.debug("Запрос общих друзей для пользователей: id={} и id={}", id, otherId);
        User user = getUserOrThrow(id);
        User otherUser = getUserOrThrow(otherId);
        List<Long> userFriends = user.getFriends();
        List<Long> otherUserFriends = otherUser.getFriends();
        return storage.findAllById(userFriends.stream()
                                              .filter(otherUserFriends::contains)
                                              .collect(Collectors.toSet()));
    }

    private User getUserOrThrow(long id) {
        return storage.findById(id)
                      .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

}
