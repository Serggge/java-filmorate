package ru.yandex.practicum.filmorate.model.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import static org.junit.jupiter.api.Assertions.assertFalse;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.LocalDate;

public class UserValidatorTest {

    static Validator validator;
    User validUser;

    @BeforeAll
    public static void beforeAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    public void beforeEach() {
        validUser = User.builder()
                        .email("ivan@yandex.ru")
                        .login("ivan2000")
                        .name("Ivan")
                        .birthday(LocalDate.of(2005, 5, 5))
                        .build();
    }

    @Test
    @DisplayName("Email is blank")
    public void mustGenerateErrorWhenUserEmailIsBlank() {
        User user = validUser;
        user.setEmail("");
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Email имеет пустое значение");
    }

    @Test
    @DisplayName("Email without @")
    public void mustGenerateErrorWhenEmailNotContainsSymbolAt() {
        User user = validUser;
        user.setEmail(user.getEmail().replace("@", ""));
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Электронная почта не содержит символ: '@'");
    }

    @Test
    @DisplayName("BirthDay in future")
    public void mustGenerateErrorWhenUserBirthDayInFuture() {
        User user = validUser;
        user.setBirthday(LocalDate.now().plusDays(1));
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Дата рождения в будущем");
    }

    @Test
    @DisplayName("Login is blank")
    public void mustGenerateErrorWhenLoginIsBlank() {
        User user = validUser;
        user.setLogin("");
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Логин имеет пустое значение");
    }

    @Test
    @DisplayName("Login is null")
    public void mustGenerateErrorWhenLoginIsNull() {
        User user = validUser;
        user.setLogin(null);
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Значение логина = Null");
    }

    @Test
    @DisplayName("Login contains space")
    public void mustGenerateErrorWhenLoginContainsSpace() {
        User user = validUser;
        user.setLogin("o o");
        var violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Логин содержит пробел");
    }

}
