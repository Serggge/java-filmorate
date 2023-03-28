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

    private static int count;
    private final UserStorage storage;

    @Override
    public User create(User user) {
        user.setId(++count);
        validateUser(user);
        log.info("Создан пользователь: {}", user);
        return storage.save(user);
    }

    @Override
    public User update(User user) {
        validateUser(user);
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
        //long id = validateId(stringId);
        return getUserOrThrow(Long.parseLong(stringId));
    }

    @Override
    public User addFriend(String stringId, String stringFriendId) {
/*        long userId = validateId(stringId);
        long friendId = validateId(stringFriendId);
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);*/
        User user = getUserOrThrow(Long.parseLong(stringId));
        User friend = getUserOrThrow(Long.parseLong(stringFriendId));
        user.addFriendId(friend.getId());
        friend.addFriendId(user.getId());
        return friend;
    }

    @Override
    public User deleteFriendById(String userStrId, String friendStrId) {
/*        long userId = validateId(userStrId);
        long friendId = validateId(friendStrId);
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);*/
        User user = getUserOrThrow(Long.parseLong(userStrId));
        User friend = getUserOrThrow(Long.parseLong(friendStrId));
        boolean friendFound = user.deleteFriendId(friend.getId());
        if (!friendFound) {
            throw new DataUpdateException("Пользователь не является вашим другом");
        } else {
            friend.deleteFriendId(user.getId());
            return friend;
        }
    }

    @Override
    public List<User> getAllFriends(String id) {
/*        long userId = validateId(id);
        User user = getUserOrThrow(userId);*/
        User user = getUserOrThrow(Long.parseLong(id));
        return storage.findAllById(user.getFriends());
    }

    @Override
    public List<User> getMutualFriends(String id, String otherId) {
/*        long userId = validateId(id);
        long otherUserId = validateId(otherId);
        User user = getUserOrThrow(userId);
        User otherUser = getUserOrThrow(otherUserId);*/
        User user = getUserOrThrow(Long.parseLong(id));
        User otherUser = getUserOrThrow(Long.parseLong(otherId));
        List<Long> userFriends = user.getFriends();
        List<Long> otherUserFriends = otherUser.getFriends();
        return storage.findAllById(userFriends.stream()
                                              .filter(otherUserFriends::contains)
                                              .collect(Collectors.toSet()));
    }

    private User getUserOrThrow(long id) {
        return storage.findById(id)
                      .orElseThrow(
                              () -> new UserNotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

}
