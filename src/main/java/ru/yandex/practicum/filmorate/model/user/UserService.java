package ru.yandex.practicum.filmorate.model.user;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    Map<Integer, User> users;

    public UserService() {
        users = new HashMap<>();
    }

    public User add(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public List<User> list() {
        return new ArrayList<>(users.values());
    }
}
