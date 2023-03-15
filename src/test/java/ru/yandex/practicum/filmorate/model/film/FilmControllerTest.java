package ru.yandex.practicum.filmorate.model.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.util.Arrays;
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
        final Film returnedFilm = controller.add(newFilm);
        final int filmId = returnedFilm.getId();
        newFilm.setId(filmId);

        assertNotNull(returnedFilm, "Новый фильм не возвращается");
        assertEquals(newFilm, returnedFilm, "Фильмы не совпадают");
    }

    @Test
    void testUpdateFilm() {
        final Film newFilm = validFilm;
        Film returnedFilm = controller.add(newFilm);
        final int filmId = returnedFilm.getId();
        final Film updatedFilm = filmAfterUpdateInfo;
        updatedFilm.setId(filmId);
        returnedFilm = controller.update(updatedFilm);

        assertNotNull(returnedFilm, "Новый фильм не возвращается");
        assertEquals(updatedFilm, returnedFilm, "Фильмы не совпадают");
    }

    @Test
    void testGetFilmList() {
        final Film firstNewFilm = validFilm;
        final Film firstReturnedFilm = controller.add(firstNewFilm);
        final int firstFilmId = firstReturnedFilm.getId();
        firstNewFilm.setId(firstFilmId);
        final Film secondNewFilm = filmAfterUpdateInfo;
        final Film secondReturnedFilm = controller.add(secondNewFilm);
        final int secondFilmId = secondReturnedFilm.getId();
        secondNewFilm.setId(secondFilmId);

        assertEquals(2, controller.list().size(), "Количество фильмов не совпадает");
        assertEquals(List.of(firstNewFilm, secondNewFilm), controller.list(), "Фильмы в списке не совпадают");
    }

    @Test
    void testAddFilmWhenFilmNameIsBlank() {
        final Film newFilm = validFilm;
        newFilm.setName("");
        final ValidationException exception = assertThrows(ValidationException.class, () -> controller.add(newFilm));

        assertEquals("Название фильма не может быть пустым", exception.getMessage(),
                "Не совпадает описание ошибки");
        assertEquals(Collections.emptyList(), controller.list(), "Задача добавляется в список");
    }

    @Test
    void testAddFilmWhenDescriptionLengthMoreThan200() {
        final char[] symbolArray = new char[201];
        Arrays.fill(symbolArray, 'a');
        final String description = String.valueOf(symbolArray);
        final Film newFilm = validFilm;
        newFilm.setDescription(description);
        final ValidationException exception = assertThrows(ValidationException.class, () -> controller.add(newFilm));

        assertEquals("Максимальная длина описания — 200 символов", exception.getMessage(),
                "Не совпадает описание ошибки");
        assertEquals(Collections.emptyList(), controller.list(), "Задача добавляется в список");
    }

    @Test
    void tesAddFilmWhenReleaseDateEarlyFirstFilmRelease() {
        final Film newFilm = validFilm;
        newFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        final ValidationException exception = assertThrows(ValidationException.class, () -> controller.add(newFilm));

        assertEquals("Дата релиза фильма не может быть раньше 28 декабря 1895 года", exception.getMessage(),
                "Не совпадает описание ошибки");
        assertEquals(Collections.emptyList(), controller.list(), "Задача добавляется в список");
    }

    @Test
    void testAddFilmWhenFilmDurationNotPositive() {
        final Film newFilm = validFilm;
        newFilm.setDuration(0);
        final ValidationException exception = assertThrows(ValidationException.class, () -> controller.add(newFilm));

        assertEquals("Продолжительность фильма должна быть положительной", exception.getMessage(),
                "Не совпадает описание ошибки");
        assertEquals(Collections.emptyList(), controller.list(), "Задача добавляется в список");
    }

    @Test
    void testUpdateFilmWhenFilmNotPresent() {
        final Film newFilm = validFilm;
        final Film returnedFilm = controller.add(newFilm);
        returnedFilm.setId(0);
        final FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () ->
                controller.update(returnedFilm));

        assertEquals("Фильма с таким id не найдено", exception.getMessage(),
                "Не совпадает описание ошибки");
        assertEquals(Collections.singletonList(newFilm), controller.list(), "Задача добавляется в список");
    }

}