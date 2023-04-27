package ru.yandex.practicum.filmorate.integration.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MovieGenre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.impl.FilmDbStorage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor__ = @Autowired)
class FilmGenreDaoIntegrationTest {

    final FilmGenreStorage filmGenreStorage;
    final FilmDbStorage filmDbStorage;
    final static Film firstFilm = new Film();
    final static Film secondFilm = new Film();

    @BeforeEach
    void beforeEach() {
        setFilmsForDefaults();
    }

    @AfterEach
    void afterEach() {
        filmGenreStorage.deleteAll();
        filmDbStorage.deleteAll();
    }

    @Test
    void testSave() {
        final int genreId = new Random().nextInt(MovieGenre.values().length - 1) + 1;
        final Genre genre = new Genre(genreId);
        firstFilm.getGenres().add(genre);
        final long filmId = filmDbStorage.save(firstFilm).getId();

        filmGenreStorage.save(firstFilm);
        List<Genre> genres = new ArrayList<>(filmGenreStorage.findGenresByFilmId(filmId));

        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .containsAll(firstFilm.getGenres());
    }

    @Test
    void testFindAllById() {
        final Genre firstGenre = new Genre(3);
        final Genre secondGenre = new Genre(4);
        firstFilm.getGenres().addAll(List.of(firstGenre, secondGenre));
        final long filmId = filmDbStorage.save(firstFilm).getId();

        filmGenreStorage.save(firstFilm);
        List<Genre> genres = new ArrayList<>(filmGenreStorage.findGenresByFilmId(filmId));

        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .containsAll(firstFilm.getGenres());
    }

    @Test
    void  testDeleteByFilmId() {
        firstFilm.getGenres().addAll(List.of(new Genre(1), new Genre(2)));
        final long filmId = filmDbStorage.save(firstFilm).getId();

        filmGenreStorage.save(firstFilm);
        filmGenreStorage.deleteByFilmId(filmId);
        List<Genre> genres = filmGenreStorage.findGenresByFilmId(filmId);

        assertThat(genres)
                .isNotNull()
                .isEmpty();
    }

    private void setFilmsForDefaults() {
        firstFilm.setId(0);
        firstFilm.setName("Film One");
        firstFilm.setDescription("Description for first film");
        firstFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        firstFilm.setDuration(120);
        firstFilm.setMpa(new Mpa(1));
        firstFilm.getGenres().clear();

        secondFilm.setId(0);
        secondFilm.setName("Second film");
        secondFilm.setDescription("Description for second film");
        secondFilm.setReleaseDate(LocalDate.of(2001, 12, 31));
        secondFilm.setDuration(180);
        secondFilm.setMpa(new Mpa(2));
        secondFilm.getGenres().clear();
    }

}