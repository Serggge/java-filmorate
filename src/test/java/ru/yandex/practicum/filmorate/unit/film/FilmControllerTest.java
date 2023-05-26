package ru.yandex.practicum.filmorate.unit.film;

import org.hamcrest.Matchers;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MovieGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.*;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    static Film firstFilm;
    static Film secondFilm;
    static final Random random = new Random();
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    FilmService service;

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
    void handleReturnPopular_returnFilmListDefaultSize() throws Exception {
        when(service.getPopular(anyMap())).thenReturn(List.of(firstFilm, secondFilm));

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

        verify(service).getPopular(Collections.emptyMap());
    }

    @Test
    void handleReturnPopularWithOutParams_returnFilmList() throws Exception {
        when(service.getPopular(anyMap())).thenReturn(List.of(firstFilm, secondFilm));

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

        verify(service).getPopular(Collections.emptyMap());
    }

    @Test
    void handleReturnPopularWithParams_returnFilmList() throws Exception {
        final int count = 1;
        final int genreId = 2;
        final int year = firstFilm.getReleaseDate().getYear();
        firstFilm.getGenres().add(new Genre(genreId));
        Map<String, String> mapParams = new HashMap<>();
        mapParams.put("count", String.valueOf(count));
        mapParams.put("genreId", String.valueOf(genreId));
        mapParams.put("year", String.valueOf(year));
        when(service.getPopular(anyMap())).thenReturn(List.of(firstFilm));

        var mvcRequest = get(String.format("/films/popular?count=%d&genreId=%d&year=%d", count, genreId, year));

        mvc.perform(mvcRequest)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(mapper.writeValueAsString(List.of(firstFilm))))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is((int) firstFilm.getId())))
                .andExpect(jsonPath("$[0].name", is(firstFilm.getName())))
                .andExpect(jsonPath("$[0].description", is(firstFilm.getDescription())))
                .andExpect(jsonPath("$[0].releaseDate", is(firstFilm.getReleaseDate().toString())))
                .andExpect(jsonPath("$[0].duration", is(firstFilm.getDuration())))
                .andExpect(jsonPath("$[0].genres[0].id", is(genreId)))
                .andExpect(jsonPath("$[0].genres[0].name", is(MovieGenre.values()[genreId - 1].getName())));

        verify(service).getPopular(mapParams);
    }



    @Test
    void handleCreateNew_RequestBodyHasWrongJson_ThrowHttpMessageNotReadableEx_returnErrorMessage() throws Exception {
        lenient().when(service.create(any(Film.class))).thenReturn(firstFilm);

        var mvcRequest = post("/films").contentType(MediaType.APPLICATION_JSON).content("\"id\": 1");

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
    void handleReturnById_PathVariableWrongType_ThrowMethodArgumentTypeMismatchEx_returnErrorMsg() throws Exception {
        lenient().when(service.getById(anyLong())).thenReturn(firstFilm);

        var mvcRequest = get(String.format("/films/%s", firstFilm.getName()));

        mvc.perform(mvcRequest).andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof MethodArgumentTypeMismatchException))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("The parameter 'id' of value 'First film' " +
                        "could not be converted to type 'long'")))
                .andExpect(jsonPath("$.description", containsString("Failed to convert " +
                        "value of type 'java.lang.String' to required type 'long'")));

        verify(service, never()).getById(anyLong());
    }

    @Test
    void handleAddNew_FilmObjectHasNotValidField_ThrowMethodArgumentNotValidEx_returnErrorMessage() throws Exception {
        lenient().when(service.create(any(Film.class))).thenReturn(firstFilm);
        firstFilm.setName("");

        var mvcRequest = post("/films").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(firstFilm));

        mvc.perform(mvcRequest).andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage()
                        .contains("Field error in object 'film' on field 'name'")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("name")))
                .andExpect(jsonPath("$.description", containsString("must not be blank")));

        verify(service, never()).create(any(Film.class));
    }

    @Test
    void testMethodNotAllowed_ThrowHttpRequestMethodNotSupportedException_returnErrorResponse() throws Exception {
        mvc.perform(patch("/films").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(firstFilm)))
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

    static void setFilmsForDefaults() {
        firstFilm.setId(random.nextInt(32) + 1);
        firstFilm.setName("First film");
        firstFilm.setDescription("Description first");
        firstFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        firstFilm.setDuration(120);
        firstFilm.setMpa(new Mpa(1));
        firstFilm.clearLikes();

        secondFilm.setId(firstFilm.getId() + 1);
        secondFilm.setName("Second film");
        secondFilm.setDescription("Description second");
        secondFilm.setReleaseDate(LocalDate.of(2020, 2, 2));
        secondFilm.setDuration(200);
        secondFilm.setMpa(new Mpa(2));
        secondFilm.clearLikes();
    }

}