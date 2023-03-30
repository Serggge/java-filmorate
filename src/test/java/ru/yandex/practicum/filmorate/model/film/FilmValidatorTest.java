package ru.yandex.practicum.filmorate.model.film;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;

public class FilmValidatorTest {

    static Validator validator;
    static Film film;
    static Random random;

    @BeforeAll
    public static void beforeAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        random = new Random();
        film = new Film();
        setFilmForDefaults();
    }

    @BeforeEach
    public void beforeEach() {
        setFilmForDefaults();
    }

    @Test
    @DisplayName("name is blank")
    public void mustGenerateErrorWhenFilmNameIsBlank() {
        film.setName("");
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Создан фильм с пустым названием");
    }

    @Test
    @DisplayName("name is null")
    public void mustGenerateErrorWhenFilmNameIsNull() {
        film.setName(null);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Создан фильм со значением null в поле name");
    }

    @Test
    @DisplayName("description is null")
    public void mustGenerateErrorWhenFilmDescriptionIsNull() {
        film.setDescription(null);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Создан фильм со значением null в поле description");
    }

    @Test
    @DisplayName("description > 200 symbols")
    public void mustGenerateErrorWhenFilmDescriptionLengthIsMoreThan200Symbols() {
        final char[] symbolArray = new char[201];
        Arrays.fill(symbolArray, 'a');
        final String longDescription = String.valueOf(symbolArray);
        film.setDescription(longDescription);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Описание фильма более 200 символов");
    }

    @Test
    @DisplayName("releaseDate before first film")
    public void mustGenerateErrorWhenFilmReleaseDateEarlyThanFirstFilmRelease() {
        film.setName(null);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Дата релиза фильма раньше релиза первого в истории фильма");
    }

    @Test
    @DisplayName("duration not positive")
    public void mustGenerateErrorWhenFilmDurationNotPositive() {
        film.setDuration(0);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Продолжительность фильма отрицательная или 0");
    }

    static void setFilmForDefaults() {
        film.setId(random.nextInt(32) + 1);
        film.setName("First film");
        film.setDescription("Description first");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.clearLikes();
    }

}
