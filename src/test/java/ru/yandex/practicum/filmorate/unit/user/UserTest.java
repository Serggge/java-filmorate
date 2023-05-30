package ru.yandex.practicum.filmorate.unit.user;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.model.User;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    static Validator validator;
    static User user;
    static Random random;

    @BeforeAll
    static void beforeAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        random = new Random();
        user = new User();
        setUserForDefaults();
        factory.close();
    }

    @BeforeEach
    void beforeEach() {
        setUserForDefaults();
    }

    @Test
    @DisplayName("Email is blank")
    void mustGenerateErrorWhenUserEmailIsBlank() {
        user.setEmail("");
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Email имеет пустое значение");
    }

    @Test
    @DisplayName("Email without @")
    void mustGenerateErrorWhenEmailNotContainsSymbolAt() {
        user.setEmail(user.getEmail().replace("@", ""));
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Электронная почта не содержит символ: '@'");
    }

    @Test
    @DisplayName("BirthDay in future")
    void mustGenerateErrorWhenUserBirthDayInFuture() {
        user.setBirthday(LocalDate.now().plusDays(1));
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Дата рождения в будущем");
    }

    @Test
    @DisplayName("Login is blank")
    void mustGenerateErrorWhenLoginIsBlank() {
        user.setLogin("");
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Логин имеет пустое значение");
    }

    @Test
    @DisplayName("Login is null")
    void mustGenerateErrorWhenLoginIsNull() {
        user.setLogin(null);
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Значение логина = Null");
    }

    @Test
    @DisplayName("Login contains space")
    void mustGenerateErrorWhenLoginContainsSpace() {
        user.setLogin("o . o");
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Логин содержит пробел");
    }

    @Test
    void mustAddUserIdInFriendList() {
        final long friendId = random.nextInt(32) + 1;
        user.addFriendId(friendId);
        final Set<Long> friends = user.getFriends();

        assertFalse(friends.isEmpty());
        assertEquals(1, friends.size());
        assertEquals(Set.of(friendId), friends);
        assertTrue(friends.contains(friendId));
    }

    @Test
    void mustDeleteFriendIdFromFriendList() {
        final long friendId = random.nextInt(32) + 1;
        user.addFriendId(friendId);
        user.deleteFriend(friendId);
        final Set<Long> friends = user.getFriends();

        assertTrue(friends.isEmpty());
        assertEquals(Collections.emptySet(), friends);
        assertFalse(friends.contains(friendId));
    }

    @Test
    void mustReturnFriendList() {
        final Set<Long> expected = new HashSet<>();
        for (long i = 1; i <= 10; i++) {
            user.addFriendId(i);
            expected.add(i);
        }
        final Set<Long> returned = user.getFriends();

        assertFalse(returned.isEmpty());
        assertEquals(expected.size(), returned.size());
        assertEquals(expected, returned);
        assertTrue(returned.containsAll(expected));
    }

    @Test
    void mustDeleteAllFriends() {
        final long friendId = random.nextInt(32) + 1;
        user.addFriendId(friendId);
        user.clearFriendList();
        final Set<Long> cleared = user.getFriends();

        assertTrue(cleared.isEmpty());
    }

    static void setUserForDefaults() {
        user.setId(random.nextInt(32) + 1);
        user.setEmail("ivan2000@yandex.ru");
        user.setLogin("Ivan2000");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.clearFriendList();
    }

}
