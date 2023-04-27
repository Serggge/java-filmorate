package ru.yandex.practicum.filmorate.integration.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MovieGenre;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorage;
import java.util.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor__ = @Autowired)
class GenreDaoIntegrationTest {

    final GenreStorage genreStorage;

    @Test
    void findById() {
        final int id = 1;

        final Optional<Genre> genreOptional = genreStorage.findById(id);

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre)
                                .hasFieldOrPropertyWithValue("name", MovieGenre.values()[id - 1].getName())
                                .hasFieldOrPropertyWithValue("name", "Комедия"));
    }

    @Test
    void findAll() {
        final List<Genre> allMovieGenres = Arrays.stream(MovieGenre.values())
                .map(movieGenre -> new Genre(movieGenre.ordinal() + 1))
                .collect(Collectors.toList());

        final Collection<Genre> genres = genreStorage.findAll();

        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .hasSize(MovieGenre.values().length)
                .containsAll(allMovieGenres)
                .isEqualTo(allMovieGenres);
    }

    @Test
    void existsById() {
        final int id = new Random().nextInt(MovieGenre.values().length - 1) + 1;

        assertTrue(genreStorage.existsById(id));
    }

}
