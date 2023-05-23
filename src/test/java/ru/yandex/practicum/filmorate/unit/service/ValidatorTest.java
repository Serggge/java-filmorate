package ru.yandex.practicum.filmorate.unit.service;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.Validator;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidatorTest {
    @Test
    void testValidateUser() {
        assertNull(Validator.validateUser(new User()).getName());
    }

    @Test
    void testValidateUser2() {
        User user = new User();
        user.setName("foo");
        Validator.validateUser(user);
    }

    @Test
    void testValidateUser3() {
        User user = new User();
        user.setName("");
        assertNull(Validator.validateUser(user).getName());
    }

    @Test
    void testValidateFilm() {
        LocalDate releaseDate = LocalDate.of(1970, 1, 1);
        Validator.validateFilm(
                new Film(1L, "Name", "The characteristics of someone or something", releaseDate, 1, new Mpa(1)));
    }

    @Test
    void testValidateFilm2() {
        LocalDate releaseDate = LocalDate.ofYearDay(1, 1);
        assertThrows(ValidationException.class, () -> Validator.validateFilm(
                new Film(1L, "Name", "The characteristics of someone or something", releaseDate, 1, new Mpa(1))));
    }

    @Test
    void testValidateExistFilm() {
        assertThrows(FilmNotFoundException.class, () -> Validator.validateExistFilm(new ArrayList<>(), 1L));
    }

    @Test
    void testValidateExistFilm2() {
        ArrayList<Long> filmIdList = new ArrayList<>();
        filmIdList.add(1L);
        Validator.validateExistFilm(filmIdList, 1L);
    }

    @Test
    void testValidateExistUser() {
        assertThrows(FilmNotFoundException.class, () -> Validator.validateExistUser(new ArrayList<>(), 1L));
    }

    @Test
    void testValidateExistUser2() {
        ArrayList<Long> userIdList = new ArrayList<>();
        userIdList.add(1L);
        Validator.validateExistUser(userIdList, 1L);
    }

    @Test
    void testValidateExistReview() {
        assertThrows(FilmNotFoundException.class, () -> Validator.validateExistReview(new ArrayList<>(), 1L));
    }

    @Test
    void testValidateExistReview2() {
        ArrayList<Long> reviewIdList = new ArrayList<>();
        reviewIdList.add(1L);
        Validator.validateExistReview(reviewIdList, 1L);
    }
}

