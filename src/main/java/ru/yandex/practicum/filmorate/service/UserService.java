package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserService {

    User addNewUser(User user);
    User updateIncomingUser(User user);
    List<User> returnAllUsers();

}
