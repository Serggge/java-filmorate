package ru.yandex.practicum.filmorate.model.user;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.model.User;
import static org.junit.jupiter.api.Assertions.assertFalse;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Random;

public class UserTest {

    static Validator validator;
    static User user;
    static Random random;

    @BeforeAll
    public static void beforeAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        random = new Random();
        user = new User();
        setUserForDefaults();
        factory.close();
    }

    @BeforeEach
    public void beforeEach() {
        setUserForDefaults();
    }

    @Test
    @DisplayName("Email is blank")
    public void mustGenerateErrorWhenUserEmailIsBlank() {
        user.setEmail("");
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Email имеет пустое значение");
    }

    @Test
    @DisplayName("Email without @")
    public void mustGenerateErrorWhenEmailNotContainsSymbolAt() {
        user.setEmail(user.getEmail().replace("@", ""));
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Электронная почта не содержит символ: '@'");
    }

    @Test
    @DisplayName("BirthDay in future")
    public void mustGenerateErrorWhenUserBirthDayInFuture() {
        user.setBirthday(LocalDate.now().plusDays(1));
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Дата рождения в будущем");
    }

    @Test
    @DisplayName("Login is blank")
    public void mustGenerateErrorWhenLoginIsBlank() {
        user.setLogin("");
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Логин имеет пустое значение");
    }

    @Test
    @DisplayName("Login is null")
    public void mustGenerateErrorWhenLoginIsNull() {
        user.setLogin(null);
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Значение логина = Null");
    }

    @Test
    @DisplayName("Login contains space")
    public void mustGenerateErrorWhenLoginContainsSpace() {
        user.setLogin("o . o");
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Логин содержит пробел");
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
