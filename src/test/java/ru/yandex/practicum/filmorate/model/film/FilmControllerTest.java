package ru.yandex.practicum.filmorate.model.film;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.List;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    FilmService service;
    Film firstFilm;
    Film secondFilm;

    @BeforeEach
    public void beforeEach() {
        firstFilm = Film.builder()
                        .name("First film")
                        .description("Description first")
                        .releaseDate(LocalDate.of(2000, 1, 1))
                        .duration(120)
                        .build();
        secondFilm = Film.builder()
                         .name("Second film")
                         .description("Description second")
                         .releaseDate(LocalDate.of(2020, 2, 2))
                         .duration(200)
                         .build();
    }

    @Test
    void handlePostFilms_addNewFilm_ReturnAdded() throws Exception {
        when(service.addNewFilm(any(Film.class))).thenReturn(firstFilm);

        var mvcRequest = post("/films").contentType(MediaType.APPLICATION_JSON)
                                       .content(mapper.writeValueAsString(firstFilm))
                                       .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
           .andExpect(status().isCreated())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.name", is("First film")))
           .andExpect(jsonPath("$.description", is("Description first")))
           .andExpect(jsonPath("$.releaseDate", is("2000-01-01")))
           .andExpect(jsonPath("$.duration", is(120)));
    }

    @Test
    void handlePutFilms_updateIncomingFilm_returnUpdated() throws Exception {
        secondFilm.setId(1);
        when(service.updateIncomingFilm(any(Film.class))).thenReturn(secondFilm);

        var mvcRequest = put("/films").contentType(MediaType.APPLICATION_JSON)
                                      .content(mapper.writeValueAsString(secondFilm))
                                      .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.name", is("Second film")))
           .andExpect(jsonPath("$.description", is("Description second")))
           .andExpect(jsonPath("$.releaseDate", is("2020-02-02")))
           .andExpect(jsonPath("$.duration", is(200)))
           .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void handleGetFilms_returnAllFilms() throws Exception {
        firstFilm.setId(1);
        secondFilm.setId(2);
        when(service.returnAllFilms()).thenReturn(List.of(firstFilm, secondFilm));

        var mvcRequest = get("/films").accept(MediaType.APPLICATION_JSON);

        mvc.perform(mvcRequest)
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(content().json(mapper.writeValueAsString(List.of(firstFilm, secondFilm))))
           .andExpect(jsonPath("$", hasSize(2)))
           .andExpect(jsonPath("$[*].id", contains(1, 2)))
           .andExpect(jsonPath("$[*].name", contains("First film", "Second film")))
           .andExpect(jsonPath("$[*].description", contains("Description first", "Description second")))
           .andExpect(jsonPath("$[*].releaseDate", contains("2000-01-01", "2020-02-02")))
           .andExpect(jsonPath("$[*].duration", contains(120, 200)));
    }
}