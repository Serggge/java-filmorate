package ru.yandex.practicum.filmorate.model.film;

import org.hamcrest.Matchers;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    FilmService service;
    static Film firstFilm;
    static Film secondFilm;
    static Random random = new Random();

    @BeforeAll
    public static void beforeAll() {
        firstFilm = new Film();
        secondFilm = new Film();
        setFilmsForDefaults();
    }

    @BeforeEach
    public void beforeEach() {
        setFilmsForDefaults();
    }

    @Test
    void handleAddNew_addNewFilm_ReturnAdded() throws Exception {
        when(service.create(any(Film.class))).thenReturn(firstFilm);

        var mvcRequest = post("/films").contentType(MediaType.APPLICATION_JSON)
                                       .content(mapper.writeValueAsString(firstFilm));

        mvc.perform(mvcRequest)
           .andExpect(status().isCreated())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.name", is("First film")))
           .andExpect(jsonPath("$.description", is("Description first")))
           .andExpect(jsonPath("$.releaseDate", is("2000-01-01")))
           .andExpect(jsonPath("$.duration", is(120)));
    }

    @Test
    void handleUpdateExisting_updateIncomingFilm_returnUpdated() throws Exception {
        when(service.update(any(Film.class))).thenReturn(secondFilm);

        var mvcRequest = put("/films").contentType(MediaType.APPLICATION_JSON)
                                      .content(mapper.writeValueAsString(secondFilm));

        mvc.perform(mvcRequest)
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$.id", is((int) secondFilm.getId())))
           .andExpect(jsonPath("$.name", is(secondFilm.getName())))
           .andExpect(jsonPath("$.description", is(secondFilm.getDescription())))
           .andExpect(jsonPath("$.releaseDate", is(secondFilm.getReleaseDate().toString())))
           .andExpect(jsonPath("$.duration", is(secondFilm.getDuration())));
    }

    @Test
    void handleReturnAll_returnAllFilms() throws Exception {
        when(service.getAll()).thenReturn(List.of(firstFilm, secondFilm));

        var mvcRequest = get("/films");

        mvc.perform(mvcRequest)
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(content().json(mapper.writeValueAsString(List.of(firstFilm, secondFilm))))
           .andExpect(jsonPath("$", hasSize(2)))
           .andExpect(jsonPath("$[*].id", contains((int) firstFilm.getId(), (int) secondFilm.getId())))
           .andExpect(jsonPath("$[*].name", contains(firstFilm.getName(), secondFilm.getName())))
           .andExpect(jsonPath("$[*].description", contains(firstFilm.getDescription(),
                   secondFilm.getDescription())))
           .andExpect(jsonPath("$[*].releaseDate", contains(firstFilm.getReleaseDate().toString(),
                   secondFilm.getReleaseDate().toString())))
           .andExpect(jsonPath("$[*].duration", contains(firstFilm.getDuration(), secondFilm.getDuration())));
    }

    @Test
    public void handleReturnFilmById_returnFilmObject() throws Exception {
        when(service.getById(anyLong())).thenReturn(firstFilm);

        var mvcRequest = get("/films/" + firstFilm.getId());

        mvc.perform(mvcRequest)
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(content().json(mapper.writeValueAsString(firstFilm)))
           .andExpect(jsonPath("$", notNullValue()))
           .andExpect(jsonPath("$.id", is((int) firstFilm.getId())))
           .andExpect(jsonPath("$.name", is(firstFilm.getName())))
           .andExpect(jsonPath("$.description", is(firstFilm.getDescription())))
           .andExpect(jsonPath("$.releaseDate", is(firstFilm.getReleaseDate().toString())))
           .andExpect(jsonPath("$.duration", is(firstFilm.getDuration())));
    }

    @Test
    void handleAddUserLike_returnFilm() throws Exception {
        when(service.setLike(anyLong(), anyLong())).thenReturn(firstFilm);

        var mvcRequest = put(String.format("/films/%d/like/%d", firstFilm.getId(), random.nextInt()));

        mvc.perform(mvcRequest).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(firstFilm)))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is((int) firstFilm.getId())))
                .andExpect(jsonPath("$.name", is(firstFilm.getName())))
                .andExpect(jsonPath("$.description", is(firstFilm.getDescription())))
                .andExpect(jsonPath("$.releaseDate", is(firstFilm.getReleaseDate().toString())))
                .andExpect(jsonPath("$.duration", is(firstFilm.getDuration())));
    }

    @Test
    void handleRemoveUserLike_returnFilm() throws Exception {
        when(service.deleteLike(anyLong(), anyLong())).thenReturn(firstFilm);

        var mvcRequest = delete(String.format("/films/%d/like/%d", firstFilm.getId(), random.nextInt()));

        mvc.perform(mvcRequest).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(firstFilm)))
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is((int) firstFilm.getId())))
                .andExpect(jsonPath("$.name", is(firstFilm.getName())))
                .andExpect(jsonPath("$.description", is(firstFilm.getDescription())))
                .andExpect(jsonPath("$.releaseDate", is(firstFilm.getReleaseDate().toString())))
                .andExpect(jsonPath("$.duration", is(firstFilm.getDuration())));
    }

    @Test
    void handleReturnPopular_returnFilmList() throws Exception {
        when(service.getPopular(anyInt())).thenReturn(List.of(firstFilm, secondFilm));

        var mvcRequest = get("/films/popular");

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(List.of(firstFilm, secondFilm))))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].id", contains((int) firstFilm.getId(), (int) secondFilm.getId())))
                .andExpect(jsonPath("$[*].name", contains(firstFilm.getName(), secondFilm.getName())))
                .andExpect(jsonPath("$[*].description", contains(firstFilm.getDescription(),
                        secondFilm.getDescription())))
                .andExpect(jsonPath("$[*].releaseDate", contains(firstFilm.getReleaseDate().toString(),
                        secondFilm.getReleaseDate().toString())))
                .andExpect(jsonPath("$[*].duration", contains(firstFilm.getDuration(),
                        secondFilm.getDuration())));
    }

    @Test
    void handleAddNew_ThrowHttpMessageNotReadableException() throws Exception {
        lenient().when(service.create(any(Film.class))).thenReturn(firstFilm);

        var mvcRequest = post("/films").contentType(MediaType.APPLICATION_JSON).content("id: 1");

        mvc.perform(mvcRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() 
                                                        instanceof HttpMessageNotReadableException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage()
                                                                                .startsWith("JSON parse error")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Получен некорректный формат JSON")))
                .andExpect(jsonPath("$.description", Matchers.startsWith("JSON parse error")));
        verify(service, never()).create(any(Film.class));
    }

    @Test
    void handleUpdateFilm_ThrowHttpMessageNotReadableException() throws Exception {
        lenient().when(service.create(any(Film.class))).thenReturn(firstFilm);

        var mvcRequest = put("/films").contentType(MediaType.APPLICATION_JSON).content("id: 1");

        mvc.perform(mvcRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof HttpMessageNotReadableException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage()
                        .startsWith("JSON parse error")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Получен некорректный формат JSON")))
                .andExpect(jsonPath("$.description", Matchers.startsWith("JSON parse error")));
        verify(service, never()).create(any(Film.class));
    }



    static void setFilmsForDefaults() {
        firstFilm.setId(random.nextInt(32) + 1);
        firstFilm.setName("First film");
        firstFilm.setDescription("Description first");
        firstFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        firstFilm.setDuration(120);
        firstFilm.clearLikes();

        secondFilm.setId(firstFilm.getId() + 1);
        secondFilm.setName("Second film");
        secondFilm.setDescription("Description second");
        secondFilm.setReleaseDate(LocalDate.of(2020, 2, 2));
        secondFilm.setDuration(200);
        secondFilm.clearLikes();
    }

}