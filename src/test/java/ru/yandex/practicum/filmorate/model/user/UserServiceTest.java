package ru.yandex.practicum.filmorate.model.user;

import org.junit.jupiter.api.BeforeAll;
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
import java.util.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserStorage storage;
    @InjectMocks
    UserServiceImpl service;
    static User user;
    static User friend;
    static Random random;
    static User[] tempContainer;

    @BeforeAll
    static void beforeAll() {
        random = new Random();
        tempContainer = new User[1];
        user = new User();
        friend = new User();
        setUsersForDefaults();
    }

    @BeforeEach
    void beforeEach() {
        setUsersForDefaults();
        tempContainer[0] = null;
    }

    @Test
    void givenUserObject_whenAddNewUser_thenReturnUserObject() {
        given(storage.save(user)).willReturn(user);

        final User savedUser = service.create(user);

        verify(storage).save(user);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(user);
    }

    @Test
    void givenUserObject_whenUpdateIncomingUser_thenReturnUserObject() {
        given(storage.save(user)).willReturn(user);
        given(storage.save(friend)).willReturn(friend);

        final User savedUser = service.create(user);
        final long id = savedUser.getId();
        friend.setId(id);

        given(storage.findAllId()).willReturn(Set.of(user.getId()));

        final User updatedUser = service.update(friend);

        verify(storage).save(user);
        verify(storage).save(friend);
        verify(storage).findAllId();
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser).isEqualTo(friend);
    }

    @Test
    void givenUserList_whenReturnAllUsers_thenReturnUserList() {
        final List<User> users = List.of(user, friend);
        given(storage.findAll()).willReturn(users);

        final List<User> allUsers = service.getAll();

        verify(storage).findAll();
        assertThat(allUsers).isNotNull();
        assertThat(allUsers.size()).isEqualTo(users.size());
        assertThat(allUsers).isEqualTo(users);
    }

    @Test
    void givenUserId_whenReturnById_thenReturnUserObject() {
        given(storage.findById(anyLong())).willReturn(Optional.of(user));

        final User returned = service.getById(String.valueOf(user.getId()));

        verify(storage).findById(user.getId());
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(user);
    }

    @Test
    void givenUserId_whenReturnById_thenThrowNotFoundException() {
        given(storage.findById(anyLong())).willReturn(Optional.empty());

        final User[] users = new User[1];
        final Throwable exception = assertThrows(UserNotFoundException.class, () -> {
            users[0] = service.getById(String.valueOf(user.getId()));
        });

        verify(storage).findById(user.getId());
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", user.getId()));
        assertThat(users[0]).isNull();
    }

    @Test
    void givenUserId_whenReturnById_thenThrowIncorrectParameterException() {
        lenient().when(storage.findById(anyLong())).thenReturn(Optional.of(user));

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class, () -> {
           tempContainer[0] = service.getById("char");
        });

        verify(storage, never()).findById(anyLong());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(IncorrectParameterException.class);
        assertThat(exception.getParam()).isEqualTo("id");
        assertThat(exception.getDescription()).isEqualTo("Идентификатор не числовой");
    }

    @Test
    void givenUserIdNotNumberType_whenReturnById_thenThrowIncorrectParameterException() {
        lenient().when(storage.findById(anyLong())).thenReturn(Optional.of(user));

        final IncorrectParameterException exception = assertThrows(IncorrectParameterException.class, () -> {
            tempContainer[0] = service.getById("char");
        });

        verify(storage, never()).findById(anyLong());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(IncorrectParameterException.class);
        assertThat(exception.getParam()).isEqualTo("id");
        assertThat(exception.getDescription()).isEqualTo("Идентификатор не числовой");
    }

    @Test
    void givenUserIdAndFriendId_whenAddFriend_thenAddFriendIdIntoSetIdAndReturnFriend() {
        given(storage.findById(user.getId())).willReturn(Optional.of(user));
        given(storage.findById(friend.getId())).willReturn(Optional.of(friend));

        final User returned = service.addFriend(String.valueOf(user.getId()), String.valueOf(friend.getId()));

        verify(storage).findById(user.getId());
        verify(storage).findById(friend.getId());
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(friend);
        assertThat(user.getFriends().size()).isEqualTo(1);
        assertThat(user.getFriends()).isEqualTo(List.of(friend.getId()));
    }

    @Test
    void givenUserNotPresentIdAndFriendId_whenAddFriend_thenThrowNotFoundExceptionNotAddedToFriends() {
        given(storage.findById(user.getId())).willReturn(Optional.empty());
        lenient().when(storage.findById(friend.getId())).thenReturn(Optional.of(friend));

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            tempContainer[0] = service.addFriend(String.valueOf(user.getId()), String.valueOf(friend.getId()));
        });

        verify(storage).findById(user.getId());
        verify(storage, never()).findById(friend.getId());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", user.getId()));
        assertThat(user.getFriends().size()).isEqualTo(0);
    }

    @Test
    void givenUserIdAndFriendNotPresentId_whenAddFriend_thenThrowNotFoundExceptionNotAddedToFriends() {
        given(storage.findById(user.getId())).willReturn(Optional.of(user));
        given(storage.findById(friend.getId())).willReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            tempContainer[0] = service.addFriend(String.valueOf(user.getId()), String.valueOf(friend.getId()));
        });

        verify(storage).findById(user.getId());
        verify(storage).findById(friend.getId());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", friend.getId()));
        assertThat(user.getFriends().size()).isEqualTo(0);
    }

    @Test
    void givenUserIdAndFriendId_whenDeleteFriendById_thenDeleteFriendIdFromFriendSetReturnFriend() {
        when(storage.findById(user.getId())).thenReturn(Optional.of(user));
        when(storage.findById(friend.getId())).thenReturn(Optional.of(friend));

        user.addFriendId(friend.getId());

        assertThat(user.getFriends().size()).isEqualTo(1);
        assertThat(user.getFriends()).isEqualTo(List.of(friend.getId()));

        final User returned = service.deleteFriendById(String.valueOf(user.getId()), String.valueOf(friend.getId()));

        verify(storage).findById(user.getId());
        verify(storage).findById(friend.getId());
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(friend);
        assertThat(user.getFriends().size()).isEqualTo(0);
        assertThat(user.getFriends()).isEqualTo(Collections.emptyList());
    }

    @Test
    void givenUserIdAndFriendIdWhichNotPresentAtFriendSet_whenDeleteFriendById_thenThrowNotFoundException() {
        when(storage.findById(user.getId())).thenReturn(Optional.of(user));
        when(storage.findById(friend.getId())).thenReturn(Optional.of(friend));

        Throwable exception = assertThrows(UserNotFoundException.class, () -> {
            tempContainer[0] = service.deleteFriendById(String.valueOf(user.getId()), String.valueOf(friend.getId()));
        });

        verify(storage).findById(user.getId());
        verify(storage).findById(friend.getId());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь не является вашим другом");
    }

    @Test
    void givenUserNotPresentIdAndFriendId_whenDeleteFriendById_thenThrowNotFoundExceptionNotAddedToFriends() {
        given(storage.findById(user.getId())).willReturn(Optional.empty());
        lenient().when(storage.findById(friend.getId())).thenReturn(Optional.of(friend));

        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            tempContainer[0] = service.deleteFriendById(String.valueOf(user.getId()), String.valueOf(friend.getId()));
        });

        verify(storage).findById(user.getId());
        verify(storage, never()).findById(friend.getId());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", user.getId()));
        assertThat(user.getFriends().size()).isEqualTo(0);
    }

    @Test
    void givenUserIdAndFriendNotPresentId_whenDeleteFriendById_thenThrowNotFoundExceptionNotAddedToFriends() {
        given(storage.findById(user.getId())).willReturn(Optional.of(user));
        given(storage.findById(friend.getId())).willReturn(Optional.empty());

        friend.setId(friend.getId());
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            tempContainer[0] = service.deleteFriendById(String.valueOf(user.getId()), String.valueOf(friend.getId()));
        });

        verify(storage).findById(user.getId());
        verify(storage).findById(friend.getId());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", friend.getId()));
        assertThat(user.getFriends().size()).isEqualTo(0);
    }

    @Test
    void givenUserId_whenGetAllFriends_thenReturnFriendList() {

    }

     static void setUsersForDefaults() {
        user.setId(random.nextInt(32) + 1);
        user.setEmail("ivan2000@yandex.ru");
        user.setLogin("Ivan2000");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.clearFriendList();

        friend.setId(user.getId() + 1);
        friend.setEmail("peter666@google.com");
        friend.setLogin("Peter666");
        friend.setName("Peter");
        friend.setBirthday(LocalDate.of(2002, 2, 2));
        friend.clearFriendList();
    }

}
