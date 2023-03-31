package ru.yandex.practicum.filmorate.user;

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
    void givenUserObject_whenCreateUser_thenReturnUserObject() {
        given(storage.save(any(User.class))).willReturn(user);

        final User savedUser = service.create(user);

        verify(storage).save(user);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(user);
    }

    @Test
    void givenUserHasEmptyName_whenCreateUser_thenReturnUserHasNameEqualsToLogin() {
        given(storage.save(any(User.class))).willReturn(user);

        user.setName("");
        final User returned = service.create(user);

        verify(storage).save(user);
        assertThat(returned).isNotNull();
        assertThat(returned.getName()).isEqualTo(user.getLogin());
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

        final User returned = service.getById(user.getId());

        verify(storage).findById(user.getId());
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(user);
    }

    @Test
    void givenUserId_whenReturnById_thenThrowNotFoundException() {
        given(storage.findById(anyLong())).willReturn(Optional.empty());

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer[0] = service.getById(user.getId()));

        verify(storage).findById(user.getId());
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", user.getId()));
        assertThat(tempContainer[0]).isNull();
    }

    @Test
    void givenUserIdAndFriendId_whenAddFriend_thenAddFriendIdIntoSetIdAndReturnFriend() {
        given(storage.findById(user.getId())).willReturn(Optional.of(user));
        given(storage.findById(friend.getId())).willReturn(Optional.of(friend));

        final User returned = service.addFriend(user.getId(), friend.getId());

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

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer[0] = service.addFriend(user.getId(), friend.getId()));

        verify(storage).findById(user.getId());
        verify(storage).findById(anyLong());
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

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer[0] = service.addFriend(user.getId(), friend.getId()));

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

        final User returned = service.deleteFriendById(user.getId(), friend.getId());

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

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer[0] = service.deleteFriendById(user.getId(), friend.getId()));

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

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer[0] = service.deleteFriendById(user.getId(), friend.getId()));

        verify(storage).findById(user.getId());
        verify(storage).findById(anyLong());
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
        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer[0] = service.deleteFriendById(user.getId(), friend.getId()));

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
        given(storage.findAllById(anyIterable())).willReturn(List.of(friend));
        given(storage.findById(anyLong())).willReturn(Optional.of(user));

        user.addFriendId(friend.getId());
        final List<User> friendList = service.getAllFriends(user.getId());

        verify(storage).findAllById(user.getFriends());
        verify(storage).findById(user.getId());
        assertThat(friendList).isNotNull();
        assertThat(friendList.size()).isEqualTo(1);
        assertThat(friendList).isEqualTo(List.of(friend));
    }

    @Test
    void givenUserNotPresentId_whenGetAllFriends_thenThrowUserNotFoundException() {
        given(storage.findById(anyLong())).willReturn(Optional.empty());
        lenient().when(storage.findAllById(anyIterable())).thenReturn(List.of(friend));

        final List<User> tempContainer = new ArrayList<>();
        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer.addAll(service.getAllFriends(user.getId())));

        verify(storage).findById(user.getId());
        verify(storage, never()).findAllById(anyIterable());
        assertThat(tempContainer.size()).isEqualTo(0);
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", user.getId()));
    }

    @Test
    void givenUserIdAndFriendId_whenGetMutualFriends_thenReturnMutualFriends() {
        User mutualFriend = User.builder().id(friend.getId() + 1).email("dima07@mailbox.org").name("Dmitry")
                .login("DmitryDima").birthday(LocalDate.of(1980, 9, 26)).build();
        given(storage.findAllById(anyIterable())).willReturn(List.of(mutualFriend));
        given(storage.findById(user.getId())).willReturn(Optional.of(user));
        given(storage.findById(friend.getId())).willReturn(Optional.of(friend));

        user.addFriendId(mutualFriend.getId());
        friend.addFriendId(mutualFriend.getId());
        final List<User> mutualFriendList = service.getMutualFriends(user.getId(), friend.getId());

        verify(storage).findAllById(anyIterable());
        verify(storage).findById(user.getId());
        verify(storage).findById(friend.getId());
        assertThat(mutualFriendList).isNotNull();
        assertThat(mutualFriendList.size()).isEqualTo(1);
        assertThat(mutualFriendList).isEqualTo(List.of(mutualFriend));
        assertThat(user.getFriends().size()).isEqualTo(1);
        assertThat(user.getFriends()).isEqualTo(List.of(mutualFriend.getId()));
        assertThat(friend.getFriends().size()).isEqualTo(1);
        assertThat(friend.getFriends()).isEqualTo(List.of(mutualFriend.getId()));
        assertThat(user.getFriends()).isEqualTo(friend.getFriends());
    }

    @Test
    void givenUserNotPresentIdAndFriendId_whenGetMutualFriends_thenThrowUserNotFoundException() {
        given(storage.findById(user.getId())).willReturn(Optional.empty());
        lenient().when(storage.findById(friend.getId())).thenReturn(Optional.of(friend));

        final List<User> tempContainer = new ArrayList<>();
        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer.addAll(service.getMutualFriends(user.getId(), friend.getId())));

        verify(storage).findById(user.getId());
        verify(storage).findById(anyLong());
        assertThat(tempContainer.size()).isEqualTo(0);
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", user.getId()));
    }

    @Test
    void givenUserIdAndFriendNotPresentId_whenGetMutualFriends_thenThrowUserNotFoundException() {
        given(storage.findById(user.getId())).willReturn(Optional.of(user));
        given(storage.findById(friend.getId())).willReturn(Optional.empty());

        final List<User> tempContainer = new ArrayList<>();
        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
            tempContainer.addAll(service.getMutualFriends(user.getId(), friend.getId())));

        verify(storage).findById(user.getId());
        verify(storage).findById(friend.getId());
        assertThat(tempContainer.size()).isEqualTo(0);
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Пользователь с id=%d не найден", friend.getId()));
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
