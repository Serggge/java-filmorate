package ru.yandex.practicum.filmorate.model.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.service.user.UserServiceImpl;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserStorage storage;
    @InjectMocks
    UserServiceImpl service;
    User firsUser;
    User secondUser;

    @BeforeEach
    void beforeEach() {
        firsUser = User.builder()
                       .email("ivan2000@yandex.ru")
                       .login("Ivan2000")
                       .name("Ivan")
                       .birthday(LocalDate.of(2000, 1, 1))
                       .build();
        secondUser = User.builder()
                         .email("peter666@google.com")
                         .login("Peter666")
                         .name("Peter")
                         .birthday(LocalDate.of(2002, 2, 2))
                         .build();
    }

    @Test
    void givenUserObject_whenAddNewUser_thenReturnUserObject() {
        given(storage.save(any(User.class))).willReturn(firsUser);

        final User savedUser = service.create(firsUser);

        verify(storage).save(firsUser);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(firsUser);
    }

    @Test
    void givenUserObject_whenUpdateIncomingUser_thenReturnUserObject() {
        given(storage.save(firsUser)).willReturn(firsUser);
        given(storage.findById(anyLong())).willReturn(Optional.of(firsUser));
        given(storage.save(secondUser)).willReturn(secondUser);

        final User savedUser = service.create(firsUser);
        final long id = savedUser.getId();
        secondUser.setId(id);
        final User updatedUser = service.update(secondUser);

        verify(storage).save(firsUser);
        verify(storage).save(secondUser);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser).isEqualTo(secondUser);
    }

    @Test
    void givenUserList_whenReturnAllUsers_thenReturnUserList() {
        final List<User> users = List.of(firsUser, secondUser);
        given(storage.findAll()).willReturn(users);

        final List<User> allUsers = service.getAll();

        verify(storage).findAll();
        assertThat(allUsers).isNotNull();
        assertThat(allUsers.size()).isEqualTo(users.size());
        assertThat(allUsers).isEqualTo(users);
    }

    @Test
    void givenUserId_whenReturnById_thenReturnUserObject() {
        final User user = firsUser;
        final int randomInt = new Random().nextInt(32) + 1;
        user.setId(randomInt);
        given(storage.findById(randomInt)).willReturn(Optional.of(user));

        final User returned = service.getById(String.valueOf(randomInt));

        verify(storage).findById(randomInt);
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(user);
    }

    @Test
    void givenUserId_whenReturnById_thenThrowNotFoundException() {
        final int randomInt = new Random().nextInt(32) + 1;
        final User[] users = new User[1];
        given(storage.findById(randomInt)).willReturn(Optional.empty());

        final Throwable exception = assertThrows(UserNotFoundException.class, () -> {
            users[0] = service.getById(String.valueOf(randomInt));
        });

        verify(storage).findById(randomInt);
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", randomInt));
        assertThat(users[0]).isNull();
    }

    @Test
    void givenUserId_whenReturnById_thenThrowIncorrectParameterException() {
        final int randomInt = new Random().nextInt(32) + 1;
        final User user = firsUser;
        user.setId(randomInt);
        final User[] users = new User[1];
        lenient().when(storage.findById(randomInt)).thenReturn(Optional.of(user));

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class, () -> {
           users[0] = service.getById("s");
        });

        verify(storage, never()).findById(anyLong());
        assertThat(users[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(IncorrectParameterException.class);
        assertThat(exception.getParam()).isEqualTo("id");
        assertThat(exception.getDescription()).isEqualTo("Идентификатор не числовой");
    }

    @Test
    void givenUserIdNotNumberType_whenReturnById_thenThrowIncorrectParameterException() {
        final String id = "s";
        final User[] returned = new User[1];
        lenient().when(storage.findById(anyLong())).thenReturn(Optional.of(firsUser));

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class, () -> {
            returned[0] = service.getById(id);
        });

        verify(storage, never()).findById(anyLong());
        assertThat(returned[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(IncorrectParameterException.class);
        assertThat(exception.getParam()).isEqualTo("id");
        assertThat(exception.getDescription()).isEqualTo("Идентификатор не числовой");
    }

    @Test
    void givenUserIdEqualToZero_whenReturnById_thenThrowIncorrectParameterException() {
        final String id = "0";
        final User[] returned = new User[1];
        lenient().when(storage.findById(anyLong())).thenReturn(Optional.of(firsUser));

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class, (() -> {
            returned[0] = service.getById(id);
        }));

        verify(storage, never()).findById(anyLong());
        assertThat(returned[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(IncorrectParameterException.class);
        assertThat(exception.getParam()).isEqualTo("id");
        assertThat(exception.getDescription()).isEqualTo("Идентификатор должен быть больше 0");
    }

    @Test
    void givenUserIdAndFriendId_whenAddFriend_thenAddFriendIdIntoSetIdAndReturnFriend() {
        final User user = firsUser;
        final int userId = new Random().nextInt(32) + 1;
        user.setId(userId);
        final User friend = secondUser;
        final int friendId = userId + 1;
        friend.setId(friendId);
        given(storage.findById(userId)).willReturn(Optional.of(user));
        given(storage.findById(friendId)).willReturn(Optional.of(friend));

        final User returned = service.addFriend(String.valueOf(userId), String.valueOf(friendId));

        verify(storage).findById(userId);
        verify(storage).findById(friendId);
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(friend);
        assertThat(user.getFriends().size()).isEqualTo(1);
        assertThat(user.getFriends()).isEqualTo(List.of(friend.getId()));
    }

    @Test
    void givenUserNotPresentIdAndFriendId_whenAddFriend_thenThrowNotFoundExceptionNotAddedToFriends() {
        final User user = firsUser;
        final int userId = new Random().nextInt(32);
        user.setId(userId);
        final User friend = secondUser;
        final int friendId = userId + 1;
        friend.setId(friendId);
        final User[] returned = new User[1];
        given(storage.findById(userId)).willReturn(Optional.empty());
        lenient().when(storage.findById(friendId)).thenReturn(Optional.of(friend));

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            returned[0] = service.addFriend(String.valueOf(userId), String.valueOf(friendId));
        });

        verify(storage).findById(userId);
        verify(storage, never()).findById(friendId);
        assertThat(returned[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", userId));
        assertThat(user.getFriends().size()).isEqualTo(0);
    }

    @Test
    void givenUserIdAndFriendNotPresentId_whenAddFriend_thenThrowNotFoundExceptionNotAddedToFriends() {
        final User user = firsUser;
        final int userId = new Random().nextInt(32);
        user.setId(userId);
        final User friend = secondUser;
        final int friendId = userId + 1;
        friend.setId(friendId);
        final User[] returned = new User[1];
        given(storage.findById(userId)).willReturn(Optional.of(user));
        given(storage.findById(friendId)).willReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            returned[0] = service.addFriend(String.valueOf(userId), String.valueOf(friendId));
        });

        verify(storage).findById(userId);
        verify(storage).findById(friendId);
        assertThat(returned[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", friendId));
        assertThat(user.getFriends().size()).isEqualTo(0);
    }

    @Test
    void givenUserIdAndFriendId_whenDeleteFriendById_thenDeleteFriendIdFromFriendSetReturnFriend() {
        final User user = firsUser;
        final int userId = new Random().nextInt(32);
        user.setId(userId);
        final User friend = secondUser;
        final int friendId = userId + 1;
        friend.setId(friendId);
        user.addFriendId(friend.getId());
        when(storage.findById(userId)).thenReturn(Optional.of(user));
        when(storage.findById(friendId)).thenReturn(Optional.of(friend));

        assertThat(user.getFriends().size()).isEqualTo(1);
        assertThat(user.getFriends()).isEqualTo(List.of(friend.getId()));

        final User returned = service.deleteFriendById(String.valueOf(userId), String.valueOf(friendId));

        verify(storage).findById(userId);
        verify(storage).findById(friendId);
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(friend);
        assertThat(user.getFriends().size()).isEqualTo(0);
        assertThat(user.getFriends()).isEqualTo(Collections.emptyList());
    }

    @Test
    void givenUserIdAndFriendIdWhichNotPresentAtFriendSet_whenDeleteFriendById_thenThrowNotFoundException() {
        final User user = firsUser;
        final int userId = new Random().nextInt(32);
        user.setId(userId);
        final User friend = secondUser;
        final int friendId = userId + 1;
        friend.setId(friendId);
        final User[] returned = new User[1];
        when(storage.findById(userId)).thenReturn(Optional.of(user));
        when(storage.findById(friendId)).thenReturn(Optional.of(friend));

        Throwable exception = assertThrows(UserNotFoundException.class, () -> {
            returned[0] = service.deleteFriendById(String.valueOf(userId), String.valueOf(friendId));
        });

        verify(storage).findById(userId);
        verify(storage).findById(friendId);
        assertThat(returned[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь не является вашим другом");
    }

    @Test
    void givenUserNotPresentIdAndFriendId_whenDeleteFriendById_thenThrowNotFoundExceptionNotAddedToFriends() {
        final User user = firsUser;
        final int userId = new Random().nextInt(32);
        user.setId(userId);
        final User friend = secondUser;
        final int friendId = userId + 1;
        friend.setId(friendId);
        final User[] returned = new User[1];
        given(storage.findById(userId)).willReturn(Optional.empty());
        lenient().when(storage.findById(friendId)).thenReturn(Optional.of(friend));

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            returned[0] = service.deleteFriendById(String.valueOf(userId), String.valueOf(friendId));
        });

        verify(storage).findById(userId);
        verify(storage, never()).findById(friendId);
        assertThat(returned[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", userId));
        assertThat(user.getFriends().size()).isEqualTo(0);
    }

    @Test
    void givenUserIdAndFriendNotPresentId_whenDeleteFriendById_thenThrowNotFoundExceptionNotAddedToFriends() {
        final User user = firsUser;
        final int userId = new Random().nextInt(32) + 1;
        user.setId(userId);
        final User friend = secondUser;
        final int friendId = userId + 1;
        friend.setId(friendId);
        final User[] returned = new User[1];
        given(storage.findById(userId)).willReturn(Optional.of(user));
        given(storage.findById(friendId)).willReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            returned[0] = service.deleteFriendById(String.valueOf(userId), String.valueOf(friendId));
        });

        verify(storage).findById(userId);
        verify(storage).findById(friendId);
        assertThat(returned[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", friendId));
        assertThat(user.getFriends().size()).isEqualTo(0);
    }



}
