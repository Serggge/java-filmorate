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

public class FilmValidatorTest {

    static Validator validator;
    Film validFilm;

    @BeforeAll
    public static void beforeAll() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    public void beforeEach() {
        validFilm = Film.builder()
                        .name("DefaultName")
                        .description("DefaultDescription")
                        .releaseDate(LocalDate.of(2000, 1, 1))
                        .duration(120)
                        .build();
    }

    @Test
    @DisplayName("name is blank")
    public void mustGenerateErrorWhenFilmNameIsBlank() {
        Film film = validFilm;
        film.setName("");
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Создан фильм с пустым названием");
    }

    @Test
    @DisplayName("name is null")
    public void mustGenerateErrorWhenFilmNameIsNull() {
        Film film = validFilm;
        film.setName(null);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Создан фильм со значением null в поле name");
    }

    @Test
    @DisplayName("description is null")
    public void mustGenerateErrorWhenFilmDescriptionIsNull() {
        Film film = validFilm;
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
        Film film = validFilm;
        film.setDescription(longDescription);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Описание фильма более 200 символов");
    }

    @Test
    @DisplayName("releaseDate before first film")
    public void mustGenerateErrorWhenFilmReleaseDateEarlyThanFirstFilmRelease() {
        Film film = validFilm;
        film.setName(null);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Дата релиза фильма раньше релиза первого в истории фильма");
    }

    @Test
    @DisplayName("duration not positive")
    public void mustGenerateErrorWhenFilmDurationNotPositive() {
        Film film = validFilm;
        film.setDuration(0);
        var violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Продолжительность фильма отрицательная или 0");
    }

}
