package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserService {

    User create(User user);

    User update(User user);

    List<User> getAll();

    User getById(long id);

    User addFriend(long userId, long friendId);

    User deleteFriendById(long userId, long friendId);

    List<User> getAllFriends(long userId);

    List<User> getMutualFriends(long id, long otherId);

    boolean existsById(long id);

}
