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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor__ = @Autowired)
class FilmGenreDaoIntegrationTest {

    static final Film firstFilm = new Film();
    static final Film secondFilm = new Film();
    final FilmGenreStorage filmGenreStorage;
    final FilmDbStorage filmStorage;

    @BeforeEach
    void beforeEach() {
        setFilmsForDefaults();
    }

    @AfterEach
    void afterEach() {
        filmGenreStorage.deleteAll();
        filmStorage.deleteAll();
    }

    @Test
    void testSave() {
        final int genreId = new Random().nextInt(MovieGenre.values().length - 1) + 1;
        final Genre genre = new Genre(genreId);
        firstFilm.addGenre(genre);
        final long filmId = filmStorage.save(firstFilm).getId();

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
        final long filmId = filmStorage.save(firstFilm).getId();

        filmGenreStorage.save(firstFilm);
        List<Genre> genres = new ArrayList<>(filmGenreStorage.findGenresByFilmId(filmId));

        assertThat(genres)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .containsAll(firstFilm.getGenres());
    }

    @Test
    void testDeleteByFilmId() {
        firstFilm.getGenres().addAll(List.of(new Genre(1), new Genre(2)));
        final long filmId = filmStorage.save(firstFilm).getId();

        filmGenreStorage.save(firstFilm);
        filmGenreStorage.deleteByFilmId(filmId);
        List<Genre> genres = filmGenreStorage.findGenresByFilmId(filmId);

        assertThat(genres)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void testFindAllByGenre() {
        Random random = new Random();
        Genre genre = new Genre(random.nextInt(6) + 1);
        firstFilm.addGenre(genre);
        secondFilm.addGenre(genre);
        filmStorage.save(firstFilm);
        filmStorage.save(secondFilm);
        filmGenreStorage.save(firstFilm);
        filmGenreStorage.save(secondFilm);

        final List<Long> foundedIds = filmGenreStorage.findAllByGenre(genre.getId());

        assertThat(foundedIds)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .contains(firstFilm.getId(), secondFilm.getId());
    }

    @Test
    void testFindAll_returnMapOfFilmsIdsAndHisGenres() {
        Random random = new Random();
        Genre genre = new Genre(random.nextInt(5) + 1);
        Genre otherGenre = new Genre(genre.getId() + 1);
        firstFilm.addGenre(genre);
        secondFilm.addGenre(otherGenre);
        filmStorage.save(firstFilm);
        filmStorage.save(secondFilm);
        filmGenreStorage.save(firstFilm);
        filmGenreStorage.save(secondFilm);
        Set<Long> filmsIds = Set.of(firstFilm.getId(), secondFilm.getId());

        Map<Long, Set<Genre>> filmGenres = filmGenreStorage.findAll(filmsIds);

        assertThat(filmGenres)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .hasEntrySatisfying(firstFilm.getId(),set ->
                        assertThat(set)
                        .isNotEmpty()
                        .hasSize(1)
                        .contains(genre))
                .hasEntrySatisfying(secondFilm.getId(), set ->
                        assertThat(set)
                                .isNotEmpty()
                                .hasSize(1)
                                .contains(otherGenre));
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