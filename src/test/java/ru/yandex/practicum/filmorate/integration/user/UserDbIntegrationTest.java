package ru.yandex.practicum.filmorate.integration.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.impl.UserDbStorage;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor__ = @Autowired)
class UserDbIntegrationTest {

    final UserDbStorage userStorage;
    static User user = new User();
    static User friend = new User();

    @BeforeEach
    void beforeEach() {
        setUsersForDefaults();
        userStorage.deleteAll();
    }

    @AfterEach
    void afterEach() {
        userStorage.deleteAll();
    }

    @Test
    void testSave() {
        assertThat(user.getId()).isZero();

        final User saved = userStorage.save(user);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotZero();
    }

    @Test
    void testFindById() {
        User saved = userStorage.save(user);

        final Optional<User> userOptional = userStorage.findById(saved.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", saved.getId()));
    }

    @Test
    void testFindAll() {
        userStorage.save(user);
        userStorage.save(friend);

        final List<User> users = userStorage.findAll();

        assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .isEqualTo(List.of(user, friend));
    }

    @Test
    void testFindAllById() {
        final long userId = userStorage.save(user).getId();
        final long friendId = userStorage.save(friend).getId();

        final List<User> users = userStorage.findAllById(List.of(userId, friendId));

        assertThat(users)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .isEqualTo(List.of(user, friend));
    }

    @Test
    void testDeleteById() {
        final long userId = userStorage.save(user).getId();

        userStorage.deleteById(userId);
        Optional<User> optionalUser = userStorage.findById(userId);

        assertThat(optionalUser).isNotPresent();
    }

    @Test
    void testDeleteAllById() {
        final long userId = userStorage.save(user).getId();
        final long friendId = userStorage.save(friend).getId();

        userStorage.deleteAllById(List.of(userId, friendId));
        final List<User> users = userStorage.findAll();

        assertThat(users)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void testExistsById() {
        assertFalse(userStorage.existsById(user.getId()));

        final long userId = userStorage.save(user).getId();

        assertTrue(userStorage.existsById(userId));
    }

    static void setUsersForDefaults() {
        user.setId(0);
        user.setEmail("ivan2000@yandex.ru");
        user.setLogin("Ivan2000");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        friend.setId(0);
        friend.setEmail("peter666@google.com");
        friend.setLogin("Peter666");
        friend.setName("Peter");
        friend.setBirthday(LocalDate.of(2002, 2, 2));
    }

}