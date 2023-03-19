package ru.yandex.practicum.filmorate.model.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository repository;
    @InjectMocks
    UserServiceImpl service;
    User firsUser;
    User secondUser;

    @BeforeEach
    public void beforeEach() {
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
        given(repository.save(any(User.class))).willReturn(firsUser);

        User savedUser = service.addNewUser(firsUser);

        verify(repository).save(firsUser);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser).isEqualTo(firsUser);
    }

    @Test
    void givenUserObject_whenUpdateIncomingUser_thenReturnUserObject() {
        given(repository.save(firsUser)).willReturn(firsUser);
        given(repository.findById(anyLong())).willReturn(Optional.of(firsUser));
        given(repository.save(secondUser)).willReturn(secondUser);

        User savedUser = service.addNewUser(firsUser);
        long id = savedUser.getId();
        secondUser.setId(id);
        User updatedUser = service.updateIncomingUser(secondUser);

        verify(repository).save(firsUser);
        verify(repository).save(secondUser);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser).isEqualTo(secondUser);
    }

    @Test
    void givenUserList_whenReturnAllUsers_thenReturnUserList() {
        List<User> users = List.of(firsUser, secondUser);
        given(repository.findAll()).willReturn(users);

        List<User> allUsers = service.returnAllUsers();

        verify(repository).findAll();
        assertThat(allUsers).isNotNull();
        assertThat(allUsers.size()).isEqualTo(users.size());
        assertThat(allUsers).isEqualTo(users);
    }
}