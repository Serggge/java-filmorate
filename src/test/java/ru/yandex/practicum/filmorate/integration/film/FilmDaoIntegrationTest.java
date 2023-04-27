package ru.yandex.practicum.filmorate.integration.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.impl.FilmDbStorage;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor__ = @Autowired)
class FilmDaoIntegrationTest {

    final FilmDbStorage filmStorage;
    static Film firstFilm = new Film();
    static Film secondFilm = new Film();


    @BeforeEach
    void beforeEach() {
        setFilmsForDefaults();
    }

    @AfterEach
    void afterEach() {
        filmStorage.deleteAll();
    }

    @Test
    void testSaveEntity() {
        assertThat(firstFilm.getId()).isZero();
        final Film saved = filmStorage.save(firstFilm);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotZero();
        assertThat(saved.getName()).isEqualTo(firstFilm.getName());
        assertThat(saved.getDescription()).isEqualTo(firstFilm.getDescription());
        assertThat(saved.getReleaseDate()).isEqualTo(firstFilm.getReleaseDate());
        assertThat(saved.getDuration()).isEqualTo(firstFilm.getDuration());
        assertThat(saved.getMpa()).isEqualTo(firstFilm.getMpa());
    }

    @Test
    void testUpdateEntity() {
        final long id = filmStorage.save(firstFilm).getId();
        secondFilm.setId(id);

        final Film saved = filmStorage.save(secondFilm);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getName()).isEqualTo(secondFilm.getName());
        assertThat(saved.getDescription()).isEqualTo(secondFilm.getDescription());
        assertThat(saved.getReleaseDate()).isEqualTo(secondFilm.getReleaseDate());
        assertThat(saved.getDuration()).isEqualTo(secondFilm.getDuration());
        assertThat(saved.getMpa()).isEqualTo(secondFilm.getMpa());
    }

    @Test
    void testFindAll() {
        final Film savedFirst = filmStorage.save(firstFilm);
        final Film savedSecond = filmStorage.save(secondFilm);

        final List<Film> allFilms = filmStorage.findAll();

        assertThat(allFilms).isNotNull();
        assertThat(allFilms.size()).isEqualTo(2);
        assertTrue(allFilms.containsAll(List.of(savedFirst, savedSecond)));
    }

    @Test
    void testFindById() {
        final long id = filmStorage.save(firstFilm).getId();

        final Optional<Film> returned = filmStorage.findById(id);

        assertThat(returned)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", id)
                                .hasFieldOrPropertyWithValue("name", firstFilm.getName())
                                .hasFieldOrPropertyWithValue("description", firstFilm.getDescription())
                                .hasFieldOrPropertyWithValue("releaseDate", firstFilm.getReleaseDate())
                                .hasFieldOrPropertyWithValue("duration", firstFilm.getDuration())
                                .hasFieldOrPropertyWithValue("mpa", firstFilm.getMpa())
                );
    }

    @Test
    void testFindAllById() {
        final Film savedFirst = filmStorage.save(firstFilm);
        final Film savedSecond = filmStorage.save(secondFilm);

        final Collection<Film> films = filmStorage.findAllById(List.of(savedFirst.getId(), savedSecond.getId()));

        assertThat(films).isNotNull();
        assertThat(films.size()).isEqualTo(2);
        assertTrue(films.containsAll(List.of(savedFirst, savedSecond)));
    }

    @Test
    void testExistsById() {
        final Film saved = filmStorage.save(firstFilm);

        assertThat(saved.getId()).isNotEqualTo(0);
        assertTrue(filmStorage.existsById(saved.getId()));
    }

    private void setFilmsForDefaults() {
        firstFilm.setId(0);
        firstFilm.setName("Film One");
        firstFilm.setDescription("Description for first film");
        firstFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        firstFilm.setDuration(120);
        firstFilm.setMpa(new Mpa(1));

        secondFilm.setId(0);
        secondFilm.setName("Second film");
        secondFilm.setDescription("Description for second film");
        secondFilm.setReleaseDate(LocalDate.of(2001, 12, 31));
        secondFilm.setDuration(180);
        secondFilm.setMpa(new Mpa(2));
    }

}