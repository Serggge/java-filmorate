package ru.yandex.practicum.filmorate.unit.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.service.impl.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.dao.EventStorage;
import ru.yandex.practicum.filmorate.storage.dao.FriendStorage;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    static User user;
    static User friend;
    static Random random;
    static User[] tempContainer;
    @Mock
    UserStorage userStorage;
    @Mock
    FriendStorage friendStorage;
    @Mock
    EventStorage eventStorage;
    @InjectMocks
    UserServiceImpl userService;

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
    void givenUserObject_whenCreateUser_thenReturnUserObject() {
        given(userStorage.save(any(User.class))).willReturn(user);

        final User savedUser = userService.create(user);

        verify(userStorage).save(user);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(user);
    }

    @Test
    void givenUserHasEmptyName_whenCreateUser_thenReturnUserHasNameEqualsToLogin() {
        given(userStorage.save(any(User.class))).willReturn(user);

        user.setName("");
        final User returned = userService.create(user);

        verify(userStorage).save(user);
        assertThat(returned).isNotNull();
        assertThat(returned.getName()).isEqualTo(user.getLogin());
    }

    @Test
    void givenUserObject_whenUpdateIncomingUser_thenReturnUserObject() {
        given(userStorage.existsById(anyLong())).willReturn(Boolean.TRUE);
        given(userStorage.save(user)).willReturn(user);

        final User updatedUser = userService.update(user);

        verify(userStorage).existsById(user.getId());
        verify(userStorage).save(user);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser).isEqualTo(user);
    }

    @Test
    void givenUserList_whenReturnAllUsers_thenReturnUserList() {
        final List<User> users = List.of(user, friend);
        given(userStorage.findAll()).willReturn(users);

        final List<User> allUsers = userService.getAll();

        verify(userStorage).findAll();
        assertThat(allUsers).isNotNull();
        assertThat(allUsers.size()).isEqualTo(users.size());
        assertThat(allUsers).isEqualTo(users);
    }

    @Test
    void givenUserId_whenReturnById_thenReturnUserObject() {
        given(userStorage.findById(anyLong())).willReturn(Optional.of(user));

        final User returned = userService.getById(user.getId());

        verify(userStorage).findById(user.getId());
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(user);
    }

    @Test
    void givenUserId_whenReturnById_thenThrowNotFoundException() {
        given(userStorage.findById(anyLong())).willReturn(Optional.empty());

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer[0] = userService.getById(user.getId()));

        verify(userStorage).findById(user.getId());
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", user.getId()));
        assertThat(tempContainer[0]).isNull();
    }

    @Test
    void givenUserIdAndFriendId_whenAddFriend_thenAddFriendIdIntoSetIdAndReturnFriend() {
        user.setId(1);
        friend.setId(2);
        given(userStorage.existsById(user.getId())).willReturn(Boolean.TRUE);
        given(userStorage.existsById(friend.getId())).willReturn(Boolean.TRUE);
        given(friendStorage.isExist(any(Friendship.class))).willReturn(Boolean.TRUE);
        given(userStorage.findById(friend.getId())).willReturn(Optional.of(friend));

        final User returned = userService.addFriend(user.getId(), friend.getId());

        verify(userStorage).existsById(user.getId());
        verify(userStorage).existsById(friend.getId());
        verify(friendStorage).isExist(new Friendship(user.getId(), friend.getId()));
        verify(userStorage).findById(friend.getId());
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(friend);
    }

    @Test
    void givenUserNotPresentIdAndFriendId_whenAddFriend_thenThrowNotFoundExceptionNotAddedToFriends() {
        user.setId(1);
        given(userStorage.existsById(user.getId())).willReturn(Boolean.FALSE);

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer[0] = userService.addFriend(user.getId(), friend.getId()));


        verify(userStorage).existsById(user.getId());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", user.getId()));
        assertThat(user.getFriends().size()).isEqualTo(0);
    }

    @Test
    void givenUserIdAndFriendNotPresentId_whenAddFriend_thenThrowNotFoundExceptionNotAddedToFriends() {
        user.setId(1);
        friend.setId(2);
        given(userStorage.existsById(user.getId())).willReturn(Boolean.TRUE);
        given(userStorage.existsById(friend.getId())).willReturn(Boolean.FALSE);

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer[0] = userService.addFriend(user.getId(), friend.getId()));

        verify(userStorage).existsById(user.getId());
        verify(userStorage).existsById(friend.getId());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", friend.getId()));
        assertThat(user.getFriends().size()).isEqualTo(0);
    }

    @Test
    void givenUserIdAndFriendId_whenDeleteFriendById_thenDeleteFriendIdFromFriendSetReturnFriend() {
        user.setId(1);
        friend.setId(2);
        given(userStorage.existsById(user.getId())).willReturn(Boolean.TRUE);
        given(userStorage.existsById(friend.getId())).willReturn(Boolean.TRUE);
        given(friendStorage.isExist(any(Friendship.class))).willReturn(Boolean.TRUE);
        given(userStorage.findById(anyLong())).willReturn(Optional.of(friend));

        final User returned = userService.deleteFriendById(user.getId(), friend.getId());

        verify(userStorage).existsById(user.getId());
        verify(userStorage).existsById(friend.getId());
        verify(friendStorage).isExist(new Friendship(user.getId(), friend.getId()));
        verify(userStorage).findById(friend.getId());
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(friend);
        assertThat(user.getFriends().size()).isEqualTo(0);
        assertThat(user.getFriends()).isEqualTo(Collections.emptySet());
    }

    @Test
    void givenUserIdAndFriendIdWhichNotPresentAtFriendSet_whenDeleteFriendById_thenThrowNotFoundException() {
        user.setId(1);
        friend.setId(2);
        given(userStorage.existsById(user.getId())).willReturn(Boolean.TRUE);
        given(userStorage.existsById(friend.getId())).willReturn(Boolean.TRUE);
        given(friendStorage.isExist(any(Friendship.class))).willReturn(Boolean.FALSE);

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer[0] = userService.deleteFriendById(user.getId(), friend.getId()));

        verify(userStorage).existsById(user.getId());
        verify(userStorage).existsById(friend.getId());
        verify(friendStorage).isExist(new Friendship(user.getId(), friend.getId()));
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь не является вашим другом");
    }

    @Test
    void givenUserNotPresentIdAndFriendId_whenDeleteFriendById_thenThrowNotFoundExceptionNotAddedToFriends() {
        user.setId(1);
        given(userStorage.existsById(anyLong())).willReturn(Boolean.FALSE);

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer[0] = userService.deleteFriendById(user.getId(), friend.getId()));

        verify(userStorage).existsById(user.getId());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", user.getId()));
        assertThat(user.getFriends().size()).isEqualTo(0);
    }

    @Test
    void givenUserIdAndFriendNotPresentId_whenDeleteFriendById_thenThrowNotFoundExceptionNotAddedToFriends() {
        user.setId(1);
        friend.setId(2);
        given(userStorage.existsById(user.getId())).willReturn(Boolean.TRUE);
        given(userStorage.existsById(friend.getId())).willReturn(Boolean.FALSE);

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer[0] = userService.deleteFriendById(user.getId(), friend.getId()));

        verify(userStorage).existsById(user.getId());
        verify(userStorage).existsById(friend.getId());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", friend.getId()));
        assertThat(user.getFriends().size()).isEqualTo(0);
    }

    @Test
    void givenUserId_whenGetAllFriends_thenReturnFriendList() {
        user.setId(1);
        user.setId(2);
        given(userStorage.existsById(anyLong())).willReturn(Boolean.TRUE);
        given(friendStorage.findFriendsIdByUserId(anyLong())).willReturn(List.of(friend.getId()));
        given(userStorage.findAllById(anyCollection())).willReturn(List.of(friend));

        final List<User> friendList = userService.getAllFriends(user.getId());

        verify(userStorage).existsById(user.getId());
        verify(friendStorage).findFriendsIdByUserId(user.getId());
        verify(userStorage).findAllById(List.of(friend.getId()));
        assertThat(friendList).isNotNull();
        assertThat(friendList.size()).isEqualTo(1);
        assertThat(friendList).isEqualTo(List.of(friend));
    }

    @Test
    void givenUserNotPresentId_whenGetAllFriends_thenThrowUserNotFoundException() {
        given(userStorage.existsById(anyLong())).willReturn(Boolean.FALSE);

        final List<User> tempContainer = new ArrayList<>();
        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer.addAll(userService.getAllFriends(user.getId())));

        verify(userStorage).existsById(user.getId());
        assertThat(tempContainer.size()).isEqualTo(0);
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", user.getId()));
    }

    @Test
    void givenUserIdAndFriendId_whenGetMutualFriends_thenReturnMutualFriends() {
        User mutualFriend = User.builder().id(3).email("dima07@mailbox.org").name("Dmitry")
                .login("DmitryDima").birthday(LocalDate.of(1980, 9, 26)).build();
        user.setId(1);
        friend.setId(2);
        given(userStorage.existsById(user.getId())).willReturn(Boolean.TRUE);
        given(userStorage.existsById(friend.getId())).willReturn(Boolean.TRUE);
        given(friendStorage.findFriendsIdByUserId(user.getId())).willReturn(List.of(mutualFriend.getId()));
        given(friendStorage.findFriendsIdByUserId(friend.getId())).willReturn(List.of(mutualFriend.getId()));
        given(userStorage.findAllById(anyCollection())).willReturn(List.of(mutualFriend));

        final List<User> mutualFriendList = userService.getMutualFriends(user.getId(), friend.getId());

        verify(userStorage).existsById(user.getId());
        verify(userStorage).existsById(friend.getId());
        verify(friendStorage).findFriendsIdByUserId(user.getId());
        verify(friendStorage).findFriendsIdByUserId(friend.getId());
        verify(userStorage).findAllById(List.of(mutualFriend.getId()));
        assertThat(mutualFriendList).isNotNull();
        assertThat(mutualFriendList.size()).isEqualTo(1);
        assertThat(mutualFriendList).isEqualTo(List.of(mutualFriend));
    }

    @Test
    void givenUserNotPresentIdAndFriendId_whenGetMutualFriends_thenThrowUserNotFoundException() {
        given(userStorage.existsById(anyLong())).willReturn(Boolean.FALSE);

        final List<User> tempContainer = new ArrayList<>();
        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer.addAll(userService.getMutualFriends(user.getId(), friend.getId())));

        verify(userStorage).existsById(user.getId());
        assertThat(tempContainer.size()).isEqualTo(0);
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", user.getId()));
    }

    @Test
    void givenUserIdAndFriendNotPresentId_whenGetMutualFriends_thenThrowUserNotFoundException() {
        user.setId(1);
        friend.setId(2);
        given(userStorage.existsById(user.getId())).willReturn(Boolean.TRUE);
        given(userStorage.existsById(friend.getId())).willReturn(Boolean.FALSE);

        final List<User> tempContainer = new ArrayList<>();
        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer.addAll(userService.getMutualFriends(user.getId(), friend.getId())));

        verify(userStorage).existsById(user.getId());
        verify(userStorage).existsById(friend.getId());
        assertThat(tempContainer.size()).isEqualTo(0);
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", friend.getId()));
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
