package ru.yandex.practicum.filmorate.service.user;

import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.User;
import java.util.List;

public interface UserService {

    User create(User user);
    User update(User user);
    List<User> getAll();
    User getById(String id);
    User addFriend(String id, String friendId);
    User deleteFriendById(String userId, String friendId);
    List<User> getAllFriends(String id);

}
