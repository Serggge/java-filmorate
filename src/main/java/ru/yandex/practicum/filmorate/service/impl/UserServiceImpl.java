package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataUpdateException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FriendStorage;
import static ru.yandex.practicum.filmorate.service.Validator.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    @Override
    public User create(User user) {
        validateUser(user);
        User saved = userStorage.save(user);
        log.info("Создан пользователь: {}", saved);
        return saved;
    }

    @Override
    public User update(User user) {
        validateUser(user);
        if (userStorage.existsById(user.getId())) {
            User saved = userStorage.save(user);
            log.info("Пользователь обновлён: {}", saved);
            return saved;
        } else {
            throw new UserNotFoundException(String.format("Пользователь: id=%d не найден", user.getId()));
        }
    }

    @Override
    public List<User> getAll() {
        log.debug("Запрошен список всех пользователей");
        return userStorage.findAll();
    }

    @Override
    public User getById(long id) {
        log.debug("Запрошен пользователь: id={}", id);
        return getUserOrThrow(id);
    }

    @Override
    public User addFriend(long userId, long friendId) {
        validateId(userId);
        validateId(friendId);
        Friendship friendship = new Friendship(userId, friendId);
        if (!friendStorage.isExist(friendship)) {
            friendStorage.save(friendship);
            log.info("Пользователь: id={} отправил запрос на дружбу пользователю: id={}", userId, friendId);
        } else if (!friendStorage.isConfirmed(friendship)) {
            friendStorage.confirm(friendship);
            log.info("Пользователь: id={} и пользователь: id={} теперь друзья", userId, friendId);
        } else {
            throw new DataUpdateException("Пользователи уже являются друзьями");
        }
        return getUserOrThrow(friendId);
    }

    @Override
    public User deleteFriendById(long userId, long friendId) {
        validateId(userId);
        validateId(friendId);
        Friendship friendship = new Friendship(userId, friendId);
        if (friendStorage.isExist(friendship)) {
            friendStorage.cancel(friendship);
            log.info("Пользователь: id={} и пользователь: id={} больше не являются друзьями", userId, friendId);
        } else {
            throw new UserNotFoundException("Пользователь не является вашим другом");
        }
        return getUserOrThrow(friendId);
    }

    @Override
    public List<User> getAllFriends(long userId) {
        validateId(userId);
        log.debug("Запрошен список друзей для пользователя: id={}", userId);
        return userStorage.findAllById(friendStorage.findFriendsIdByUserId(userId));
    }

    @Override
    public List<User> getMutualFriends(long userId, long otherId) {
        validateId(userId);
        validateId(otherId);
        log.debug("Запрос общих друзей для пользователей: id={} и id={}", userId, otherId);
        Collection<Long> friendsId = friendStorage.findFriendsIdByUserId(userId);
        List<Long> commonId = friendStorage.findFriendsIdByUserId(otherId)
                .stream()
                .filter(friendsId::contains)
                .collect(Collectors.toList());
        return userStorage.findAllById(commonId);
    }

    @Override
    public boolean existsById(long id) {
        return userStorage.existsById(id);
    }

    private User getUserOrThrow(long id) {
        return userStorage.findById(id)
                      .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

    private void validateId(long id) {
        if (!userStorage.existsById(id)) {
            throw new UserNotFoundException(String.format("Пользователь с id=%d не найден", id));
        }
    }

}
