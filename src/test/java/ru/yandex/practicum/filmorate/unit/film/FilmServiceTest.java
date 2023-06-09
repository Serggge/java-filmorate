package ru.yandex.practicum.filmorate.unit.film;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.yandex.practicum.filmorate.util.Constants.FIRST_FILM;
import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.exception.DataUpdateException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.service.impl.FilmServiceImpl;
import ru.yandex.practicum.filmorate.storage.dao.DirectorsStorage;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;
import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    @Qualifier("filmDbStorage")
    FilmStorage filmStorage;
    @Mock
    UserService userService;
    @Mock
    FilmGenreStorage filmGenreStorage;
    @Mock
    LikeStorage likeStorage;
    @Mock
    DirectorsStorage directorsStorage;

    @InjectMocks
    FilmServiceImpl filmService;
    static Film firstFilm;
    static Film secondFilm;
    static User user;
    static Random random;
    static Film[] tempContainer;

    @BeforeAll
    static void beforeAll() {
        random = new Random();
        tempContainer = new Film[1];
        firstFilm = new Film();
        secondFilm = new Film();
        user = new User();
        settingsForDefaults();
    }

    @BeforeEach
    public void beforeEach() {
        settingsForDefaults();
    }

    @Test
    void givenFilmObject_whenAddNewFilm_thenReturnFilmObject() {
        given(filmStorage.save(any(Film.class))).willReturn(firstFilm);

        final Film savedFilm = filmService.create(firstFilm);

        verify(filmStorage).save(firstFilm);
        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm).isEqualTo(firstFilm);
    }

    @Test
    void givenFilmHasReleaseDateBeforeFirstFilm_whenCreateFilm_thenThrowValidationException() {
        lenient().when(filmStorage.save(any(Film.class))).thenReturn(firstFilm);

        firstFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        final Throwable exception = assertThrows(ValidationException.class, () ->
                tempContainer[0] = filmService.create(firstFilm));

        verify(filmStorage, never()).save(any(Film.class));
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(ValidationException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Дата релиза фильма раньше %s", FIRST_FILM));
    }

    @Test
    void givenFilmObject_whenUpdateIncomingFilm_thenReturnFilmObject() {
        firstFilm.setId(1);
        given(filmStorage.existsById(anyLong())).willReturn(Boolean.TRUE);
        given(filmStorage.update(firstFilm)).willReturn(firstFilm);

        final Film updatedFilm = filmService.update(firstFilm);

        verify(filmStorage).existsById(firstFilm.getId());
        verify(filmStorage).update(firstFilm);
        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm).isEqualTo(firstFilm);
    }

    @Test
    void givenFilmObjectNotPresentInStorage_whenUpdateFilm_thenThrowFilmNotFoundException() {
        firstFilm.setId(1);
        given(filmStorage.existsById(anyLong())).willReturn(Boolean.FALSE);

        final Throwable exception = assertThrows(FilmNotFoundException.class, () ->
                tempContainer[0] = filmService.update(firstFilm));

        verify(filmStorage).existsById(anyLong());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм с id=%d не найден", firstFilm.getId()));
    }

    @Test
    void whenGetAllFilms_thenReturnFilmList() {
        final List<Film> films = new ArrayList<>();
        films.addAll(List.of(firstFilm, secondFilm));
        given(filmStorage.findAll()).willReturn(films);

        final List<Film> allFilms = filmService.getAll();

        verify(filmStorage).findAll();
        assertThat(allFilms).isNotNull();
        assertThat(allFilms.size()).isEqualTo(films.size());
        assertThat(allFilms).isEqualTo(films);
    }

    @Test
    void givenFilmId_whenGetFilmById_thenReturnFilmObject() {
        given(filmStorage.findById(anyLong())).willReturn(Optional.of(firstFilm));

        final Film returned = filmService.getById(firstFilm.getId());

        verify(filmStorage).findById(firstFilm.getId());
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(firstFilm);
    }

    @Test
    void givenFilmIdNotPresentInStorage_whenGetFilmById_thenThrowNotFoundException() {
        given(filmStorage.findById(anyLong())).willReturn(Optional.empty());

        final Throwable exception = assertThrows(FilmNotFoundException.class, () ->
                tempContainer[0] = filmService.getById(firstFilm.getId()));

        verify(filmStorage).findById(firstFilm.getId());
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм с id=%d не найден", firstFilm.getId()));
        assertThat(tempContainer[0]).isNull();
    }

    @Test
    void givenFilmIdNotPresentInStorageAndUserId_whenSetLike_thenThrowFilmNotFoundException() {
        given(filmStorage.existsById(anyLong())).willReturn(Boolean.FALSE);
        lenient().when(userService.existsById(anyLong())).thenReturn(Boolean.TRUE);

        final Throwable exception = assertThrows(FilmNotFoundException.class, () ->
                filmService.setLike(firstFilm.getId(), user.getId()));

        verify(filmStorage).existsById(firstFilm.getId());
        verify(userService, never()).existsById(anyLong());
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм с id=%d не найден", firstFilm.getId()));
    }

    @Test
    void givenFilmIdAndUserIdNotPresent_whenSetLike_thenThrowUserNotFoundException() {
        given(filmStorage.existsById(anyLong())).willReturn(Boolean.TRUE);
        given(userService.existsById(anyLong())).willThrow(new UserNotFoundException("Пользователь не найден"));

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
                filmService.setLike(firstFilm.getId(), user.getId()));

        verify(filmStorage).existsById(firstFilm.getId());
        verify(userService).existsById(user.getId());
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь не найден");
    }

    @Test
    void givenFilmIdAndUserIdWhoNotLikedFilm_whenDeleteLike_thenThrowDataUpdateException() {
        given(filmStorage.existsById(anyLong())).willReturn(Boolean.TRUE);
        given(userService.existsById(anyLong())).willReturn(Boolean.TRUE);
        given(likeStorage.isExist(any(Like.class))).willReturn(Boolean.FALSE);

        final Throwable exception = assertThrows(DataUpdateException.class, () ->
                filmService.deleteLike(firstFilm.getId(), user.getId()));

        verify(filmStorage).existsById(firstFilm.getId());
        verify(userService).existsById(user.getId());
        verify(likeStorage).isExist(new Like(firstFilm.getId(), user.getId()));
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(DataUpdateException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь ранее не оставлял лайк");
    }

    @Test
    void givenFilmIdNotPresentInStorageAndUserId_whenDeleteLike_thenThrowFilmNotFoundException() {
        given(filmStorage.existsById(anyLong())).willReturn(Boolean.FALSE);
        lenient().when(userService.getById(anyLong())).thenReturn(user);

        final Throwable exception = assertThrows(FilmNotFoundException.class, () ->
                filmService.deleteLike(firstFilm.getId(), user.getId()));

        verify(filmStorage).existsById(firstFilm.getId());
        verify(userService, never()).getById(anyLong());
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм с id=%d не найден", firstFilm.getId()));
    }

    @Test
    void givenFilmIdAndUserIdNotPresent_whenDeleteLike_thenThrowUserNotFoundException() {
        given(filmStorage.existsById(anyLong())).willReturn(Boolean.TRUE);
        given(userService.existsById(anyLong())).willThrow(new UserNotFoundException("Пользователь не найден"));

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
                filmService.deleteLike(firstFilm.getId(), user.getId()));

        verify(filmStorage).existsById(firstFilm.getId());
        verify(userService).existsById(user.getId());
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь не найден");
    }

    @Test
    void givenCountPopularFilms_whenGetPopularByYear_thenReturnPopularListWithTopFirstFilm() {
        firstFilm.setId(1);
        secondFilm.setId(2);
        secondFilm.setReleaseDate(firstFilm.getReleaseDate());
        firstFilm.addLike(1);
        final int year = firstFilm.getReleaseDate().getYear();
        given(filmStorage.findAllByYear(anyInt())).willReturn(List.of(firstFilm.getId(), secondFilm.getId()));
        given(filmStorage.findAllById(anyCollection())).willReturn(List.of(firstFilm, secondFilm));
        final Map<String, String> yearParam = Map.of("year", String.valueOf(year));

        final List<Film> mostPopular = filmService.getPopular(yearParam);

        verify(filmStorage).findAllByYear(year);
        assertThat(mostPopular).isNotNull();
        assertThat(mostPopular.size()).isEqualTo(2);
        assertThat(mostPopular).isEqualTo(List.of(firstFilm, secondFilm));
    }

    @Test
    void givenCountPopularFilms_whenGetPopularByYear_thenReturnPopularListWithTopSecondFilm() {
        firstFilm.setId(1);
        secondFilm.setId(2);
        secondFilm.setReleaseDate(firstFilm.getReleaseDate());
        secondFilm.addLike(1);
        final int year = firstFilm.getReleaseDate().getYear();
        given(filmStorage.findAllByYear(anyInt())).willReturn(List.of(firstFilm.getId(), secondFilm.getId()));
        given(filmStorage.findAllById(anyCollection())).willReturn(List.of(firstFilm, secondFilm));
        final Map<String, String> yearParam = Map.of("year", String.valueOf(year));

        final List<Film> mostPopular = filmService.getPopular(yearParam);

        verify(filmStorage).findAllByYear(year);
        assertThat(mostPopular).isNotNull();
        assertThat(mostPopular.size()).isEqualTo(2);
        assertThat(mostPopular).isEqualTo(List.of(secondFilm, firstFilm));
    }

    @Test
    void givenCountPopularFilms_whenGetPopularByGenre_thenReturnPopularListWithTopFirstFilm() {
        firstFilm.setId(1);
        secondFilm.setId(2);
        final int genreId = 2;
        firstFilm.addGenre(new Genre(genreId));
        secondFilm.addGenre(new Genre(genreId));
        firstFilm.addLike(1);
        given(filmGenreStorage.findAllByGenre(genreId)).willReturn(List.of(firstFilm.getId(), secondFilm.getId()));
        given(filmStorage.findAllById(anyCollection())).willReturn(List.of(firstFilm, secondFilm));
        final Map<String, String> genreParam = Map.of("genreId", String.valueOf(genreId));

        final List<Film> mostPopular = filmService.getPopular(genreParam);

        verify(filmGenreStorage).findAllByGenre(genreId);
        assertThat(mostPopular).isNotNull();
        assertThat(mostPopular.size()).isEqualTo(2);
        assertThat(mostPopular).isEqualTo(List.of(firstFilm, secondFilm));
    }

    @Test
    void givenCountPopularFilms_whenGetPopularByGenre_thenReturnPopularListWithTopSecondFilm() {
        firstFilm.setId(1);
        secondFilm.setId(2);
        final int genreId = 2;
        firstFilm.addGenre(new Genre(genreId));
        secondFilm.addGenre(new Genre(genreId));
        secondFilm.addLike(1);
        given(filmGenreStorage.findAllByGenre(genreId)).willReturn(List.of(firstFilm.getId(), secondFilm.getId()));
        given(filmStorage.findAllById(anyCollection())).willReturn(List.of(firstFilm, secondFilm));
        final Map<String, String> genreParam = Map.of("genreId", String.valueOf(genreId));

        final List<Film> mostPopular = filmService.getPopular(genreParam);

        verify(filmGenreStorage).findAllByGenre(genreId);
        assertThat(mostPopular).isNotNull();
        assertThat(mostPopular.size()).isEqualTo(2);
        assertThat(mostPopular).isEqualTo(List.of(secondFilm, firstFilm));
    }

    static void settingsForDefaults() {
        tempContainer[0] = null;

        firstFilm.setId(0);
        firstFilm.setName("First film");
        firstFilm.setDescription("Description first");
        firstFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        firstFilm.setDuration(120);
        firstFilm.setMpa(new Mpa(1));
        firstFilm.clearLikes();

        secondFilm.setId(0);
        secondFilm.setName("Second film");
        secondFilm.setDescription("Description second");
        secondFilm.setReleaseDate(LocalDate.of(2020, 2, 2));
        secondFilm.setDuration(200);
        secondFilm.setMpa(new Mpa(2));
        secondFilm.clearLikes();

        user.setId(0);
        user.setEmail("ivan2000@yandex.ru");
        user.setLogin("Ivan2000");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

}