package ru.yandex.practicum.filmorate.model.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.when;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService service;
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
    void handlePostUsers_addNewUser_returnAdded() throws Exception {
        when(service.create(any(User.class))).thenReturn(firsUser);

        final var mvcRequest = post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(firsUser))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is("ivan2000@yandex.ru")))
                .andExpect(jsonPath("$.login", is("Ivan2000")))
                .andExpect(jsonPath("$.name", is("Ivan")))
                .andExpect(jsonPath("$.birthday", is("2000-01-01")));
    }

    @Test
    void handlePutUsers_updateIncomingUser_returnUpdated() throws Exception {
        secondUser.setId(1);
        when(service.update(any(User.class))).thenReturn(secondUser);

        final var mvcRequest = put("/users").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(secondUser))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is("peter666@google.com")))
                .andExpect(jsonPath("$.login", is("Peter666")))
                .andExpect(jsonPath("$.name", is("Peter")))
                .andExpect(jsonPath("$.birthday", is("2002-02-02")))
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void handleGetUsers_returnAllUsers() throws Exception {
        firsUser.setId(1);
        secondUser.setId(2);
        when(service.getAll()).thenReturn(List.of(firsUser, secondUser));

        final var mvcRequest = get("/users").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(List.of(firsUser, secondUser)))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].email", contains("ivan2000@yandex.ru", "peter666@google.com")))
                .andExpect(jsonPath("$[*].login", contains("Ivan2000", "Peter666")))
                .andExpect(jsonPath("$[*].name", contains("Ivan", "Peter")))
                .andExpect(jsonPath("$[*].birthday", contains("2000-01-01", "2002-02-02")))
                .andExpect(jsonPath("$[*].id", contains(1, 2)));
    }

    @Test
    void handleReturnById_returnUserObject() throws Exception {
        final User user = firsUser;
        final int randomInt = new Random().nextInt();
        user.setId(randomInt);
        final String id = String.valueOf(randomInt);
        final String jsonString = mapper.writeValueAsString(user);
        when(service.getById(id)).thenReturn(user);

        final var mvcRequest = get("/users/" + id).accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonString))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(randomInt)))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.login", is(user.getLogin())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.birthday", is(user.getBirthday()
                        .toString())));
    }

    @Test
    void handleInviteFriend_returnFriend() throws Exception {
        final User user = firsUser;
        final int userId = new Random().nextInt(32) + 1;
        user.setId(userId);
        final User friend = secondUser;
        final int friendId = userId + 1;
        friend.setId(friendId);
        final String jsonString = mapper.writeValueAsString(friend);
        when(service.addFriend(String.valueOf(userId), String.valueOf(friendId))).thenReturn(friend);

        final var mvcRequest = put(String.format("/users/%d/friends/%d", userId, friendId));

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonString))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(friendId)))
                .andExpect(jsonPath("$.email", is(friend.getEmail())))
                .andExpect(jsonPath("$.login", is(friend.getLogin())))
                .andExpect(jsonPath("$.name", is(friend.getName())))
                .andExpect(jsonPath("$.birthday", is(friend.getBirthday()
                        .toString())));
    }

    @Test
    void handleRemoveFromFriends_returnFriend() throws Exception {
        final User user = firsUser;
        final int userId = new Random().nextInt(32) + 1;
        user.setId(userId);
        final User friend = secondUser;
        final int friendId = userId + 1;
        friend.setId(friendId);
        final String jsonString = mapper.writeValueAsString(friend);
        when(service.deleteFriendById(anyString(), anyString())).thenReturn(friend);

        final var mvcRequest = delete(String.format("/users/%d/friends/%d", userId, friendId));

        mvc.perform(mvcRequest).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonString))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(friendId)))
                .andExpect(jsonPath("$.email", is(friend.getEmail())))
                .andExpect(jsonPath("$.login", is(friend.getLogin())))
                .andExpect(jsonPath("$.name", is(friend.getName())))
                .andExpect(jsonPath("$.birthday", is(friend.getBirthday().toString())));
    }

}