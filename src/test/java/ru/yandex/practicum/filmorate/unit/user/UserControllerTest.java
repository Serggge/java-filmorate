package ru.yandex.practicum.filmorate.unit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@WebMvcTest(UserController.class)
class UserControllerTest {

    static User user;
    static User friend;
    static Random random;
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService service;

    @BeforeAll
    public static void beforeAll() {
        random = new Random();
        user = new User();
        friend = new User();
        setUsersForDefaults();
    }

    @BeforeEach
    public void beforeEach() {
        setUsersForDefaults();
    }

    @Test
    void handlePostUsers_addNewUser_returnAdded() throws Exception {
        when(service.create(any(User.class))).thenReturn(user);

        final var mvcRequest = post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user));

        mvc.perform(mvcRequest)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(user)))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is((int) user.getId())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.login", is(user.getLogin())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.birthday", is(user.getBirthday().toString())));
    }

    @Test
    void handlePutUsers_updateIncomingUser_returnUpdated() throws Exception {
        when(service.update(any(User.class))).thenReturn(friend);

        final var mvcRequest = put("/users").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(friend));

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(friend)))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is((int) friend.getId())))
                .andExpect(jsonPath("$.email", is(friend.getEmail())))
                .andExpect(jsonPath("$.login", is(friend.getLogin())))
                .andExpect(jsonPath("$.name", is(friend.getName())))
                .andExpect(jsonPath("$.birthday", is(friend.getBirthday().toString())));
    }

    @Test
    void handleGetUsers_returnAllUsers() throws Exception {
        when(service.getAll()).thenReturn(List.of(user, friend));

        final var mvcRequest = get("/users").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(List.of(user, friend)));

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(List.of(user, friend))))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", contains((int) user.getId(), (int) friend.getId())))
                .andExpect(jsonPath("$[*].email", contains(user.getEmail(), friend.getEmail())))
                .andExpect(jsonPath("$[*].login", contains(user.getLogin(), friend.getLogin())))
                .andExpect(jsonPath("$[*].name", contains(user.getName(), friend.getName())))
                .andExpect(jsonPath("$[*].birthday", contains(user.getBirthday().toString(),
                        friend.getBirthday().toString())));
    }

    @Test
    void handleReturnById_returnUserObject() throws Exception {
        when(service.getById(anyLong())).thenReturn(user);

        final var mvcRequest = get("/users/" + user.getId());

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(user)))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is((int) user.getId())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.login", is(user.getLogin())))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.birthday", is(user.getBirthday().toString())));
    }

    @Test
    void handleInviteFriend_returnFriend() throws Exception {
        when(service.addFriend(anyLong(), anyLong())).thenReturn(friend);

        final var mvcRequest = put(String.format("/users/%d/friends/%d", user.getId(), friend.getId()));

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(friend)))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is((int) friend.getId())))
                .andExpect(jsonPath("$.email", is(friend.getEmail())))
                .andExpect(jsonPath("$.login", is(friend.getLogin())))
                .andExpect(jsonPath("$.name", is(friend.getName())))
                .andExpect(jsonPath("$.birthday", is(friend.getBirthday().toString())));
    }

    @Test
    void handleRemoveFromFriends_returnFriend() throws Exception {
        when(service.deleteFriendById(anyLong(), anyLong())).thenReturn(friend);

        final var mvcRequest = delete(String.format("/users/%d/friends/%d", user.getId(), friend.getId()));

        mvc.perform(mvcRequest).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(friend)))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is((int) friend.getId())))
                .andExpect(jsonPath("$.email", is(friend.getEmail())))
                .andExpect(jsonPath("$.login", is(friend.getLogin())))
                .andExpect(jsonPath("$.name", is(friend.getName())))
                .andExpect(jsonPath("$.birthday", is(friend.getBirthday().toString())));
    }

    @Test
    void handleReturnAllFriends_returnFriends() throws Exception {
        final List<User> friends = List.of(friend);
        when(service.getAllFriends(anyLong())).thenReturn(friends);

        var mvcRequest = get(String.format("/users/%d/friends", user.getId()));

        mvc.perform(mvcRequest).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(friends)))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", hasSize(friends.size())))
                .andExpect(jsonPath("$[0].id", is((int) friend.getId())))
                .andExpect(jsonPath("$[0].email", is(friend.getEmail())))
                .andExpect(jsonPath("$[0].login", is(friend.getLogin())))
                .andExpect(jsonPath("$[0].name", is(friend.getName())))
                .andExpect(jsonPath("$[0].birthday", is(friend.getBirthday().toString())));
    }

    @Test
    void handleReturnMutualFriends_returnMutualFriends() throws Exception {
        User mutualFriend = User.builder().id(friend.getId() + 1).email("dima07@mailbox.org").name("Dmitry")
                .login("DmitryDima").birthday(LocalDate.of(1980, 9, 26)).build();
        when(service.getMutualFriends(anyLong(), anyLong())).thenReturn(List.of(mutualFriend));

        var mvcRequest = get(String.format("/users/%d/friends/common/%d", user.getId(), friend.getId()));

        mvc.perform(mvcRequest).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(List.of(mutualFriend))))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is((int) mutualFriend.getId())))
                .andExpect(jsonPath("$[0].email", is(mutualFriend.getEmail())))
                .andExpect(jsonPath("$[0].login", is(mutualFriend.getLogin())))
                .andExpect(jsonPath("$[0].name", is(mutualFriend.getName())))
                .andExpect(jsonPath("$[0].birthday", is(mutualFriend.getBirthday().toString())));
    }

    @Test
    void handleAddNew_RequestBodyHasWrongJson_ThrowHttpMessageNotReadableEx_returnErrorMessage() throws Exception {
        lenient().when(service.create(any(User.class))).thenReturn(user);

        var mvcRequest = post("/users").contentType(MediaType.APPLICATION_JSON)
                .content("\"id\": 1");

        mvc.perform(mvcRequest).andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof HttpMessageNotReadableException))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        .getMessage()
                        .startsWith("JSON parse error")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Получен некорректный формат JSON")))
                .andExpect(jsonPath("$.description", Matchers.startsWith("JSON parse error")));

        verify(service, never()).create(any(User.class));
    }

    @Test
    void handleReturnById_PathVariableWrongType_ThrowMethodArgumentTypeMismatchEx_returnErrorMsg() throws Exception {
        lenient().when(service.update(any(User.class))).thenReturn(user);

        var mvcRequest = get(String.format("/users/%s", user.getName()));

        mvc.perform(mvcRequest).andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentTypeMismatchException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage()
                        .contains("Failed to convert value of type")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString(String.format(
                        "The parameter 'id' of value '%s' could not be converted to type 'long'", user.getName()))))
                .andExpect(jsonPath("$.description", containsString("Failed to convert value " +
                        "of type 'java.lang.String' to required type 'long'")));

        verify(service, never()).update(any(User.class));
    }

    @Test
    void handleAddNew_UserObjectHasNotValidField_ThrowMethodArgumentNotValidEx_returnErrorMessage() throws Exception {
        lenient().when(service.create(any(User.class))).thenReturn(user);

        user.setLogin("q q");
        var mvcRequest = post(String.format("/users")).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user));

        mvc.perform(mvcRequest).andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage()
                        .contains("Field error in object 'user' on field 'login'")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("login")))
                .andExpect(jsonPath("$.description",
                        containsString("логин не может содержать пробелы")));

        verify(service, never()).create(any(User.class));
    }

    @Test
    void testMethodNotAllowed_ThrowHttpRequestMethodNotSupportedException_returnErrorResponse() throws Exception {
        mvc.perform(patch("/users").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof HttpRequestMethodNotSupportedException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage()
                        .matches("^Request method '(POST|PUT|PATCH|DELETE)' not supported$")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", matchesPattern(
                        "^Request method '(POST|PUT|PATCH|DELETE)' not supported$")))
                .andExpect(jsonPath("$.description", matchesPattern("^(POST|PUT|PATCH|DELETE)$")));
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