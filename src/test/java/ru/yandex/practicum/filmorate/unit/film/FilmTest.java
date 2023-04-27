package ru.yandex.practicum.filmorate.unit.film;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FilmTest {

    static Validator validator;
    static Film film;
    static Random random;

    @BeforeAll
    static void beforeAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        random = new Random();
        film = new Film();
        setFilmForDefaults();
        factory.close();
    }

    @BeforeEach
    void beforeEach() {
        setFilmForDefaults();
    }

    @Test
    @DisplayName("name is blank")
    void mustGenerateErrorWhenFilmNameIsBlank() {
        film.setName("");
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Создан фильм с пустым названием");
    }

    @Test
    @DisplayName("name is null")
    void mustGenerateErrorWhenFilmNameIsNull() {
        film.setName(null);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Создан фильм со значением null в поле name");
    }

    @Test
    @DisplayName("description is null")
    void mustGenerateErrorWhenFilmDescriptionIsNull() {
        film.setDescription(null);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Создан фильм со значением null в поле description");
    }

    @Test
    @DisplayName("description > 200 symbols")
    void mustGenerateErrorWhenFilmDescriptionLengthIsMoreThan200Symbols() {
        final char[] symbolArray = new char[201];
        Arrays.fill(symbolArray, 'a');
        final String longDescription = String.valueOf(symbolArray);
        film.setDescription(longDescription);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Описание фильма более 200 символов");
    }

    @Test
    @DisplayName("releaseDate before first film")
    void mustGenerateErrorWhenFilmReleaseDateEarlyThanFirstFilmRelease() {
        film.setName(null);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Дата релиза фильма раньше релиза первого в истории фильма");
    }

    @Test
    @DisplayName("duration not positive")
    void mustGenerateErrorWhenFilmDurationNotPositive() {
        film.setDuration(0);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Продолжительность фильма отрицательная или 0");
    }

    @Test
    void mustAddUserIdInLikeSet() {
        final long userId = random.nextInt(32) + 1;
        film.addLike(userId);
        final Set<Long> result = film.getLikes();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(Set.of(userId), result);
        assertTrue(result.contains(userId));
    }

    @Test
    void mustRemoveUserIdFromLikeSet() {
        final long userId = random.nextInt(32) + 1;
        film.addLike(userId);
        film.removeLike(userId);
        final Set<Long> result = film.getLikes();

        assertTrue(result.isEmpty());
        assertEquals(Collections.emptySet(), result);
        assertFalse(result.contains(userId));
    }

    @Test
    void mustReturnLikeList() {
        final Set<Long> expected = new HashSet<>();
        for (long i = 1; i <= 10; i++) {
            film.addLike(i);
            expected.add(i);
        }
        final Set<Long> returned = film.getLikes();


        assertFalse(returned.isEmpty());
        assertEquals(expected.size(), returned.size());
        assertEquals(expected, returned);
        assertTrue(returned.containsAll(expected));
    }

    @Test
    void mustDeleteAllLikes() {
        final long userId = random.nextInt(32) + 1;
        film.addLike(userId);
        film.clearLikes();
        final Set<Long> cleared = film.getLikes();

        assertTrue(cleared.isEmpty());
    }


    static void setFilmForDefaults() {
        film.setId(random.nextInt(32) + 1);
        film.setName("First film");
        film.setDescription("Description first");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1));
        film.clearLikes();
    }

}
