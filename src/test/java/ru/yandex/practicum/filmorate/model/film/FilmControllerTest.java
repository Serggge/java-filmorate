package ru.yandex.practicum.filmorate.model.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    FilmController controller;

    @BeforeEach
    public void beforeEach() {
        controller = new FilmController();
    }

    @Test
    public void testAddFilm() {
        Film newFilm = new Film("One", "Desc", LocalDate.of(2000, 1, 1), 120);
        Film returnedFilm = controller.add(newFilm);
        int filmId = returnedFilm.getId();
        newFilm.setId(filmId);

        assertNotNull(returnedFilm, "Новый фильм не возвращается");
        assertEquals(newFilm, returnedFilm, "Фильмы не совпадают");
    }

    @Test
    public void testUpdateFilm() {
        Film newFilm = new Film("One", "Desc", LocalDate.of(2000, 1, 1), 120);
        Film returnedFilm = controller.add(newFilm);
        int filmId = returnedFilm.getId();
        Film updatedFilm = new Film("Updated", "Updated", newFilm.getReleaseDate()
                                                                 .plusDays(1), 200);
        updatedFilm.setId(filmId);
        returnedFilm = controller.update(updatedFilm);

        assertNotNull(returnedFilm, "Новый фильм не возвращается");
        assertEquals(updatedFilm, returnedFilm, "Фильмы не совпадают");
    }

    @Test
    public void testGetFilmList() {
        Film firstNewFilm = new Film("One", "Desc", LocalDate.of(2000, 1, 1), 120);
        Film firstReturnedFilm = controller.add(firstNewFilm);
        int firstFilmId = firstReturnedFilm.getId();
        firstNewFilm.setId(firstFilmId);
        Film secondNewFilm = new Film("One", "Desc", LocalDate.of(2002, 2, 2), 200);
        Film secondReturnedFilm = controller.add(secondNewFilm);
        int secondFilmId = secondReturnedFilm.getId();
        secondNewFilm.setId(secondFilmId);

        assertEquals(List.of(firstNewFilm, secondNewFilm)
                         .size(), controller.list()
                                            .size(), "Количество фильмов не совпадает");
        assertEquals(List.of(firstNewFilm, secondNewFilm), controller.list(), "Фильмы в списке не совпадают");
    }

    @Test
    public void testWhenFilmNameIsBlank() {
        Film newFilm = new Film(" ", "Desc", LocalDate.of(2000, 1, 1), 120);
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.add(newFilm));

        assertEquals("Название фильма не может быть пустым", exception.getMessage(),
                "Не совпадает описание ошибки");
        assertEquals(Collections.emptyList(), controller.list(), "Задача добавляется в список");
    }

}