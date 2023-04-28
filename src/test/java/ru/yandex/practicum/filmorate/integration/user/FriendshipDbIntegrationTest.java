package ru.yandex.practicum.filmorate.integration.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;
import ru.yandex.practicum.filmorate.storage.dao.impl.UserDbStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class FriendshipDbIntegrationTest {

    final FriendStorage friendStorage;
    static UserDbStorage userStorage;
    static User user = new User();
    static User friend = new User();

    @Autowired
    public FriendshipDbIntegrationTest(FriendStorage friendStorage, UserDbStorage userDbStorage) {
        this.friendStorage = friendStorage;
        userStorage = userDbStorage;
        setUsersForDefaults();
        userStorage.save(user);
        userStorage.save(friend);
    }

    @BeforeEach
    void beforeEach() {
        setUsersForDefaults();
    }

    @AfterEach
    void afterEach() {
        friendStorage.deleteAll();
    }

    @Test
    void save() {
    }

    @Test
    void findAllById() {
    }

    @Test
    void findById() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void isExist() {
    }

    @Test
    void isConfirmed() {
    }

    @Test
    void confirm() {
    }

    static void setUsersForDefaults() {
        user.setId(0);
        user.setEmail("ivan2000@yandex.ru");
        user.setLogin("Ivan2000");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.clearFriendList();

        friend.setId(0);
        friend.setEmail("peter666@google.com");
        friend.setLogin("Peter666");
        friend.setName("Peter");
        friend.setBirthday(LocalDate.of(2002, 2, 2));
        friend.clearFriendList();
    }
}