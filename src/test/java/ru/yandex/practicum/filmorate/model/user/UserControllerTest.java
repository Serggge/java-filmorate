package ru.yandex.practicum.filmorate.model.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    UserController userController;
    User firsUser;
    User secondUser;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
        firsUser = new User("ivan@yandex.ru", "Ivan2000", "Ivan",
                LocalDate.of(1990, 1, 1));
        secondUser = new User("peter@yandex.ru", "Peter123", "Peter",
                LocalDate.of(1995, 2, 2));
    }

    @Test
    void testAddUser() {
        final User newUser = firsUser;
        final User returnedUser = userController.addUser(newUser);
        final int userId = returnedUser.getId();
        newUser.setId(userId);

        assertNotNull(returnedUser, "Пользователь не возвращается");
        assertEquals(newUser, returnedUser, "Пользователи не совпадают");
    }

    @Test
    void testUpdateUser() {
        final User newUser = firsUser;
        User returnedUser = userController.addUser(newUser);
        final int userId = returnedUser.getId();
        returnedUser.setName("Ivan Ivanov");
        final User userAfterUpdateInfo = userController.updateUser(returnedUser);

        assertNotNull(userAfterUpdateInfo, "Пользователь не возвращается");
        assertEquals(returnedUser, userAfterUpdateInfo, "Пользователи не совпадают");
    }

    @Test
    void testGetUserList() {
        final User firstNewUser = firsUser;
        final User firstReturnedUser = userController.addUser(firstNewUser);
        final int firstUserId = firstReturnedUser.getId();
        firstNewUser.setId(firstUserId);
        final User secondNewUser = secondUser;
        final User secondReturnedUser = userController.addUser(secondNewUser);
        final int secondUserId = secondReturnedUser.getId();
        secondNewUser.setId(secondUserId);

        assertEquals(2, userController.returnAllUsers().size(), "Количество пользователей не совпадает");
        assertEquals(List.of(firstNewUser, secondNewUser), userController.returnAllUsers(),
                "Пользователи в списке не совпадают");
    }

    @Test
    void testAddUserWhenUserNameIsBlank() {
        final User newUser = firsUser;
        final String userLogin = newUser.getLogin();
        newUser.setName("");
        final User returnedUser = userController.addUser(newUser);

        assertEquals(userLogin, returnedUser.getName(), "Значение имени не совпадает с логином пользователя");
    }

    @Test
    void testUpdateUserWhenUserNotPresent() {
        final User newUser = firsUser;
        final User returnedUser = userController.addUser(newUser);
        returnedUser.setId(0);

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                userController.updateUser(returnedUser));
        assertEquals("Пользователя с таким id не существует", exception.getMessage(),
                "Не совпадает описание ошибки");
        assertEquals(Collections.singletonList(newUser), userController.returnAllUsers(),
                "Пользователь добавляется в список");
    }

}