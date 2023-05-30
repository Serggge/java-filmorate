package ru.yandex.practicum.filmorate.integration.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dao.impl.*;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor__ = @Autowired)
class FilmDaoIntegrationTest {

    static final Film firstFilm = new Film();
    static final Film secondFilm = new Film();
    static final Random random = new Random();
    final FilmDbStorage filmStorage;
    final FilmGenreDbStorage filmGenreStorage;
    final DirectorsStorageImpl directorsStorage;
    final LikeDbStorage likeStorage;
    final UserDbStorage userStorage;


    @BeforeEach
    void beforeEach() {
        setFilmsForDefaults();
    }

    @AfterEach
    void afterEach() {
        filmStorage.deleteAll();
        filmGenreStorage.deleteAll();
        directorsStorage.deleteAll();
        likeStorage.deleteAll();
        userStorage.deleteAll();
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
    void testUpdateFilm() {
        final Film saved = filmStorage.save(firstFilm);
        final long filmId = saved.getId();
        secondFilm.setId(filmId);

        final Film updated = filmStorage.update(secondFilm);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(filmId);
        assertThat(updated.getName()).isEqualTo(secondFilm.getName());
        assertThat(updated.getDescription()).isEqualTo(secondFilm.getDescription());
        assertThat(updated.getReleaseDate()).isEqualTo(secondFilm.getReleaseDate());
        assertThat(updated.getDuration()).isEqualTo(secondFilm.getDuration());
        assertThat(updated.getMpa()).isEqualTo(secondFilm.getMpa());
    }

    @Test
    void testFindAll() {
        final Film savedFirst = filmStorage.save(firstFilm);
        final Film savedSecond = filmStorage.save(secondFilm);

        final List<Film> allFilms = filmStorage.findAll();

        assertThat(allFilms).isNotNull();
        assertThat(allFilms.size()).isEqualTo(2);
        assertTrue(allFilms.contains(savedFirst));
        assertTrue(allFilms.contains(savedSecond));
    }

    @Test
    void testFindById() {
        Genre genre = new Genre(random.nextInt(6) + 1);
        firstFilm.addGenre(genre);
        User user = User.builder()
                .login("Peter555")
                .name("Peter")
                .email("peter@ya.ru")
                .birthday(LocalDate.of(2010, 1, 1))
                .build();
        userStorage.save(user);
        Director director = new Director(0, "Director");
        directorsStorage.create(director);
        final long id = filmStorage.save(firstFilm).getId();
        Like like = new Like(firstFilm.getId(),user.getId());
        likeStorage.save(like);

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
    void testFindAllIds() {
        final Film savedFirst = filmStorage.save(firstFilm);
        final Film savedSecond = filmStorage.save(secondFilm);

        List<Long> foundIds = filmStorage.findAllIds();

        assertThat(foundIds)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .contains(savedFirst.getId(), savedSecond.getId());
    }

    @Test
    void testFindAllById() {
        final Film savedFirst = filmStorage.save(firstFilm);
        final Film savedSecond = filmStorage.save(secondFilm);

        final List<Film> films = filmStorage.findAllById(List.of(savedFirst.getId(), savedSecond.getId()));

        assertThat(films).isNotNull();
        assertThat(films.size()).isEqualTo(2);
        assertTrue(films.contains(savedFirst));
        assertTrue(films.contains(savedSecond));
    }

    @Test
    void testExistsById() {
        final Film saved = filmStorage.save(firstFilm);

        assertThat(saved.getId()).isNotEqualTo(0);
        assertTrue(filmStorage.existsById(saved.getId()));
    }

    @Test
    void testFindBySubString() {
        final String partOfName = "FILM";
        final Film savedFirst = filmStorage.save(firstFilm);
        final Film savedSecond = filmStorage.save(secondFilm);

        final List<Long> foundedByName = filmStorage.findBySubString(partOfName);

        assertThat(foundedByName)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .contains(savedFirst.getId(), savedSecond.getId());
    }

    @Test
    void testFindByParams_byYearParam() {
        final Film saved = filmStorage.save(firstFilm);

        final List<Long> foundedIds = filmStorage.findAllByYear(firstFilm.getReleaseDate().getYear());

        assertThat(foundedIds)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(saved.getId());
    }

    @Test
    void testFindByParams_byGenreParam() {
        final int genreId = new Random().nextInt(MovieGenre.values().length) + 1;
        firstFilm.getGenres().add(new Genre(genreId));
        final Film savedFilm = filmStorage.save(firstFilm);
        filmGenreStorage.save(savedFilm);

        final List<Long> foundedIds = filmGenreStorage.findAllByGenre(genreId);

        assertThat(foundedIds)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(savedFilm.getId());
    }

    @Test
    void testFindPopular_returnListIdWithTopFirstFilm() {
        filmStorage.save(firstFilm);
        filmStorage.save(secondFilm);
        final User user = User.builder()
                .login("Serggge69")
                .email("serggge69@yandex.ru")
                .name("Sergey")
                .birthday(LocalDate.of(1984, 1, 14))
                .build();
        final long userId = userStorage.save(user).getId();
        likeStorage.save(new Like(firstFilm.getId(), userId));

        List<Long> foundedIds = filmStorage.findPopular(2);

        assertThat(foundedIds)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .containsExactly(firstFilm.getId(), secondFilm.getId());
    }

    @Test
    void testFindPopular_returnListIdWithTopSecondFilm() {
        filmStorage.save(firstFilm);
        filmStorage.save(secondFilm);
        final User user = User.builder()
                .login("Serggge69")
                .email("serggge69@yandex.ru")
                .name("Sergey")
                .birthday(LocalDate.of(1984, 1, 14))
                .build();
        final long userId = userStorage.save(user).getId();
        likeStorage.save(new Like(secondFilm.getId(), userId));

        List<Long> foundedIds = filmStorage.findPopular(2);

        assertThat(foundedIds)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .containsExactly(secondFilm.getId(), firstFilm.getId());
    }

    @Test
    void testFindPopular_incomingCountOne_returnSingleFilmIdAndEqualToSecondFilmId() {
        filmStorage.save(firstFilm);
        filmStorage.save(secondFilm);
        final User user = User.builder()
                .login("Serggge69")
                .email("serggge69@yandex.ru")
                .name("Sergey")
                .birthday(LocalDate.of(1984, 1, 14))
                .build();
        final long userId = userStorage.save(user).getId();
        likeStorage.save(new Like(secondFilm.getId(), userId));

        List<Long> foundedIds = filmStorage.findPopular(1);

        assertThat(foundedIds)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(secondFilm.getId());
    }

    @Test
    void testFindByYearAndGenreParams_returnTwoFilms() {
        final LocalDate date = LocalDate.of(2022, 1, 1);
        firstFilm.setReleaseDate(date);
        secondFilm.setReleaseDate(date);
        final Genre genre = new Genre(random.nextInt(6) + 1);
        firstFilm.addGenre(genre);
        secondFilm.addGenre(genre);
        filmStorage.save(firstFilm);
        filmStorage.save(secondFilm);
        filmGenreStorage.save(firstFilm);
        filmGenreStorage.save(secondFilm);

        List<Long> foundedIds = filmStorage.findByYearAndGenre(date.getYear(), genre.getId());

        assertThat(foundedIds)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .contains(firstFilm.getId(), secondFilm.getId());
    }

    @Test
    void testFindByYearAndGenreParams_givenFirstFilmHasNotEqualYearParam_returnOneFilmId() {
        final LocalDate date = LocalDate.of(2022, 1, 1);
        firstFilm.setReleaseDate(date.minusYears(1));
        secondFilm.setReleaseDate(date);
        final Genre genre = new Genre(random.nextInt(6) + 1);
        firstFilm.addGenre(genre);
        secondFilm.addGenre(genre);
        filmStorage.save(firstFilm);
        filmStorage.save(secondFilm);
        filmGenreStorage.save(firstFilm);
        filmGenreStorage.save(secondFilm);

        List<Long> foundedIds = filmStorage.findByYearAndGenre(date.getYear(), genre.getId());

        assertThat(foundedIds)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(secondFilm.getId());
    }

    @Test
    void testFindByYearAndGenreParams_givenFirstFilmHasNotEqualGenreParam_returnOneFilmId() {
        final LocalDate date = LocalDate.of(2022, 1, 1);
        firstFilm.setReleaseDate(date.minusYears(1));
        secondFilm.setReleaseDate(date);
        final Genre genre = new Genre(random.nextInt(5) + 1);
        final Genre notEqualGenre = new Genre(genre.getId() + 1);
        firstFilm.addGenre(notEqualGenre);
        secondFilm.addGenre(genre);
        filmStorage.save(firstFilm);
        filmStorage.save(secondFilm);
        filmGenreStorage.save(firstFilm);
        filmGenreStorage.save(secondFilm);

        List<Long> foundedIds = filmStorage.findByYearAndGenre(date.getYear(), genre.getId());

        assertThat(foundedIds)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(secondFilm.getId());
    }

    @Test
    void testDeleteById() {
        final long id = filmStorage.save(firstFilm).getId();

        filmStorage.delete(id);
        Optional<Film> optionalFilm = filmStorage.findById(id);

        assertThat(optionalFilm).isNotPresent();
    }

    private void setFilmsForDefaults() {
        firstFilm.setId(0);
        firstFilm.setName("Film One");
        firstFilm.setDescription("Description for first film");
        firstFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        firstFilm.setDuration(120);
        firstFilm.setMpa(new Mpa(1));
        firstFilm.getGenres().clear();
        firstFilm.getDirectors().clear();
        firstFilm.getLikes().clear();

        secondFilm.setId(0);
        secondFilm.setName("Second film");
        secondFilm.setDescription("Description for second film");
        secondFilm.setReleaseDate(LocalDate.of(2001, 12, 31));
        secondFilm.setDuration(180);
        secondFilm.setMpa(new Mpa(2));
        secondFilm.getGenres().clear();
        secondFilm.getDirectors().clear();
        secondFilm.getLikes().clear();
    }

}