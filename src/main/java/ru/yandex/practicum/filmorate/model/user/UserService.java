package ru.yandex.practicum.filmorate.model.user;

import java.util.List;

public interface UserService {

    User addUser(User user);
    User updateUser(User user);
    List<User> returnAllUsers();

}
