package ru.yandex.practicum.filmorate.integration.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendStorage;
import ru.yandex.practicum.filmorate.storage.dao.impl.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
class FriendshipDbIntegrationTest {

    static UserDbStorage userStorage;
    static final User user = new User();
    static final User friend = new User();
    final FriendStorage friendStorage;

    @Autowired
    public FriendshipDbIntegrationTest(FriendStorage friendStorage, UserDbStorage userDbStorage) {
        this.friendStorage = friendStorage;
        userStorage = userDbStorage;
        userStorage.deleteAll();
        setUsersForDefaults();
        userStorage.save(user);
        userStorage.save(friend);
    }

    @AfterEach
    void afterEach() {
        friendStorage.deleteAll();
    }

    @Test
    void testSaveAndFindFriendsId() {
        final Friendship friendship = new Friendship(user.getId(), friend.getId());

        friendStorage.save(friendship);

        final List<Long> friendsForUser = friendStorage.findFriendsIdByUserId(user.getId());
        final List<Long> friendsForFriend = friendStorage.findFriendsIdByUserId(friend.getId());

        assertThat(friendsForUser)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(friend.getId());
        assertThat(friendsForFriend)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void testFindFriendship() {
        final Friendship friendship = new Friendship(user.getId(), friend.getId());
        final Friendship inverseFriendship = new Friendship(friend.getId(), user.getId());
        friendStorage.save(friendship);

        final Optional<Friendship> optionalFriendship = friendStorage.find(inverseFriendship);

        assertThat(optionalFriendship)
                .hasValueSatisfying(friendShip -> assertThat(friendShip)
                        .hasFieldOrPropertyWithValue("userId", user.getId())
                        .hasFieldOrPropertyWithValue("friendId", friend.getId()));
    }

    @Test
    void testCancelFriendship() {
        final Friendship friendship = new Friendship(user.getId(), friend.getId());
        final Friendship inverseFriendship = new Friendship(friend.getId(), user.getId());
        friendStorage.save(friendship);
        assertThat(friendStorage.find(friendship)).isPresent();

        friendStorage.cancel(friendship);

        assertThat(friendStorage.find(friendship)).isNotPresent();
        assertThat(friendStorage.find(inverseFriendship)).isNotPresent();
    }

    @Test
    void testIsExist() {
        final Friendship friendship = new Friendship(user.getId(), friend.getId());
        final Friendship inverseFriendship = new Friendship(friend.getId(), user.getId());
        friendStorage.save(friendship);

        final boolean areFriends = friendStorage.isExist(friendship);

        assertThat(areFriends).isTrue();
        assertThat(friendStorage.isExist(inverseFriendship)).isTrue();
    }

    @Test
    void testIsNotConfirmed() {
        final Friendship friendship = new Friendship(user.getId(), friend.getId());
        final Friendship inverseFriendship = new Friendship(friend.getId(), user.getId());
        friendStorage.save(friendship);

        final boolean isConfirmed = friendStorage.isConfirmed(friendship);

        assertThat(isConfirmed).isFalse();
        assertThat(friendStorage.isConfirmed(inverseFriendship)).isFalse();
    }

    @Test
    void testConfirm() {
        final Friendship friendship = new Friendship(user.getId(), friend.getId());
        final Friendship inverseFriendship = new Friendship(friend.getId(), user.getId());
        friendStorage.save(friendship);

        friendStorage.confirm(inverseFriendship);

        assertThat(friendStorage.isConfirmed(friendship)).isTrue();
        assertThat(friendStorage.isConfirmed(inverseFriendship)).isTrue();
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