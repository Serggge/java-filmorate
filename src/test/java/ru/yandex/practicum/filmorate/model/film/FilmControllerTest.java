package ru.yandex.practicum.filmorate.model.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    FilmController controller;
    Film validFilm;
    Film filmAfterUpdateInfo;

    @BeforeEach
    void beforeEach() {
        controller = new FilmController();
        validFilm = new Film("FilmName", "Description",
                LocalDate.of(2000, 1, 1), 120);
        filmAfterUpdateInfo =  new Film("Updated", "Updated",
                validFilm.getReleaseDate().plusDays(1), 200);
    }

    @Test
    void testAddFilm() {
        final Film newFilm = validFilm;
        final Film returnedFilm = controller.addFilm(newFilm);
        final int filmId = returnedFilm.getId();
        newFilm.setId(filmId);

        assertNotNull(returnedFilm, "Новый фильм не возвращается");
        assertEquals(newFilm, returnedFilm, "Фильмы не совпадают");
    }

    @Test
    void testUpdateFilm() {
        final Film newFilm = validFilm;
        Film returnedFilm = controller.addFilm(newFilm);
        final int filmId = returnedFilm.getId();
        final Film updatedFilm = filmAfterUpdateInfo;
        updatedFilm.setId(filmId);
        returnedFilm = controller.updateFilm(updatedFilm);

        assertNotNull(returnedFilm, "Новый фильм не возвращается");
        assertEquals(updatedFilm, returnedFilm, "Фильмы не совпадают");
    }

    @Test
    void testGetFilmList() {
        final Film firstNewFilm = validFilm;
        final Film firstReturnedFilm = controller.addFilm(firstNewFilm);
        final int firstFilmId = firstReturnedFilm.getId();
        firstNewFilm.setId(firstFilmId);
        final Film secondNewFilm = filmAfterUpdateInfo;
        final Film secondReturnedFilm = controller.addFilm(secondNewFilm);
        final int secondFilmId = secondReturnedFilm.getId();
        secondNewFilm.setId(secondFilmId);

        assertEquals(2, controller.returnAllFilms().size(), "Количество фильмов не совпадает");
        assertEquals(List.of(firstNewFilm, secondNewFilm), controller.returnAllFilms(), "Фильмы в списке не совпадают");
    }

    @Test
    void testUpdateFilmWhenFilmNotPresent() {
        final Film newFilm = validFilm;
        final Film returnedFilm = controller.addFilm(newFilm);
        returnedFilm.setId(0);
        final FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () ->
                controller.updateFilm(returnedFilm));

        assertEquals("Фильма с таким id не найдено", exception.getMessage(),
                "Не совпадает описание ошибки");
        assertEquals(Collections.singletonList(newFilm), controller.returnAllFilms(), "Задача добавляется в список");
    }

}