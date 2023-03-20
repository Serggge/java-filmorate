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
import ru.yandex.practicum.filmorate.service.UserService;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.any;
import java.time.LocalDate;
import java.util.List;
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

        var mvcRequest = post("/users").contentType(MediaType.APPLICATION_JSON)
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

        var mvcRequest = put("/users").contentType(MediaType.APPLICATION_JSON)
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

        var mvcRequest = get("/users").contentType(MediaType.APPLICATION_JSON)
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

}