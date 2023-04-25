package ru.yandex.practicum.filmorate.film;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.yandex.practicum.filmorate.Constants.FIRST_FILM;

import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.exception.DataUpdateException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.service.impl.FilmServiceImpl;
import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    @Qualifier("filmDbStorage")
    FilmStorage storage;
    @Mock
    UserService userService;
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
        given(storage.save(any(Film.class))).willReturn(firstFilm);

        final Film savedFilm = filmService.create(firstFilm);

        verify(storage).save(firstFilm);
        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm).isEqualTo(firstFilm);
    }

    @Test
    void givenFilmHasReleaseDateBeforeFirstFilm_whenCreateFilm_thenThrowValidationException() {
        lenient().when(storage.save(any(Film.class))).thenReturn(firstFilm);

        firstFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        final Throwable exception = assertThrows(ValidationException.class, () ->
                tempContainer[0] = filmService.create(firstFilm));

        verify(storage, never()).save(any(Film.class));
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(ValidationException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Дата релиза фильма раньше %s", FIRST_FILM));
    }

    @Test
    void givenFilmObject_whenUpdateIncomingFilm_thenReturnFilmObject() {
        given(storage.save(firstFilm)).willReturn(firstFilm);
        given(storage.save(secondFilm)).willReturn(secondFilm);

        final Film savedFilm = filmService.create(firstFilm);
        secondFilm.setId(savedFilm.getId());

        given(storage.findAll()).willReturn(List.of(savedFilm));

        final Film updatedFilm = filmService.update(secondFilm);

        verify(storage).save(firstFilm);
        verify(storage).save(secondFilm);
        verify(storage).findAll();
        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm).isEqualTo(secondFilm);
    }

    @Test
    void givenFilmObjectNotPresentInStorage_whenUpdateFilm_thenThrowFilmNotFoundException() {
        given(storage.findAll()).willReturn(Collections.emptyList());
        lenient().when(storage.save(any(Film.class))).thenReturn(firstFilm);

        final Throwable exception = assertThrows(FilmNotFoundException.class, () ->
                                                            tempContainer[0] = filmService.update(firstFilm));

        verify(storage).findAll();
        verify(storage, never()).save(any(Film.class));
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм: id=%d не найден", firstFilm.getId()));
    }

    @Test
    void whenGetAllFilms_thenReturnFilmList() {
        final List<Film> films = List.of(firstFilm, secondFilm);
        given(storage.findAll()).willReturn(films);

        final List<Film> allFilms = filmService.getAll();

        verify(storage).findAll();
        assertThat(allFilms).isNotNull();
        assertThat(allFilms.size()).isEqualTo(films.size());
        assertThat(allFilms).isEqualTo(films);
    }

    @Test
    void givenFilmId_whenGetFilmById_thenReturnFilmObject() {
        given(storage.findById(anyLong())).willReturn(Optional.of(firstFilm));

        final Film returned = filmService.getById(firstFilm.getId());

        verify(storage).findById(firstFilm.getId());
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(firstFilm);
    }

    @Test
    void givenFilmIdNotPresentInStorage_whenGetFilmById_thenThrowNotFoundException() {
        given(storage.findById(anyLong())).willReturn(Optional.empty());

        final Throwable exception = assertThrows(FilmNotFoundException.class, () ->
                                                     tempContainer[0] = filmService.getById(firstFilm.getId()));

        verify(storage).findById(firstFilm.getId());
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм с id=%d не найден", firstFilm.getId()));
        assertThat(tempContainer[0]).isNull();
    }

    @Test
    void givenFilmIdAndUserId_whenSetLike_thenReturnFilmObject() {
        given(storage.findById(anyLong())).willReturn(Optional.of(firstFilm));
        given(userService.getById(anyLong())).willReturn(user);

        final Film returned = filmService.setLike(firstFilm.getId(), user.getId());

        verify(storage).findById(firstFilm.getId());
        verify(userService).getById(user.getId());
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(firstFilm);
    }

    @Test
    void givenFilmIdNotPresentInStorageAndUserId_whenSetLike_thenThrowFilmNotFoundException() {
        given(storage.findById(anyLong())).willReturn(Optional.empty());
        lenient().when(userService.getById(anyLong())).thenReturn(user);

        final Throwable exception = assertThrows(FilmNotFoundException.class, () ->
                                          tempContainer[0] = filmService.setLike(firstFilm.getId(), user.getId()));

        verify(storage).findById(firstFilm.getId());
        verify(userService, never()).getById(anyLong());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм с id=%d не найден", firstFilm.getId()));
    }

    @Test
    void givenFilmIdAndUserIdNotPresent_whenSetLike_thenThrowUserNotFoundException() {
        given(storage.findById(anyLong())).willReturn(Optional.of(firstFilm));
        given(userService.getById(anyLong())).willThrow(new UserNotFoundException("Пользователь не найден"));

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
                                    tempContainer[0] = filmService.setLike(firstFilm.getId(), user.getId()));

        verify(storage).findById(firstFilm.getId());
        verify(userService).getById(user.getId());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь не найден");
    }

    @Test
    void givenFilmIdAndUserId_whenDeleteLike_thenReturnFilmObject() {
        given(storage.findById(anyLong())).willReturn(Optional.of(firstFilm));
        given(userService.getById(anyLong())).willReturn(user);

        firstFilm.addLike(user.getId());
        final Film returned = filmService.deleteLike(firstFilm.getId(), user.getId());

        verify(storage).findById(firstFilm.getId());
        verify(userService).getById(user.getId());
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(firstFilm);
    }

    @Test
    void givenFilmIdAndUserIdWhoNotLikedFilm_whenDeleteLike_thenThrowDataUpdateException() {
        given(storage.findById(anyLong())).willReturn(Optional.of(firstFilm));
        given(userService.getById(anyLong())).willReturn(user);

        final Throwable exception = assertThrows(DataUpdateException.class, () ->
                                   tempContainer[0] = filmService.deleteLike(firstFilm.getId(), user.getId()));

        verify(storage).findById(firstFilm.getId());
        verify(userService).getById(user.getId());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(DataUpdateException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь ранее не оставлял лайк");
    }

    @Test
    void givenFilmIdNotPresentInStorageAndUserId_whenDeleteLike_thenThrowFilmNotFoundException() {
        given(storage.findById(anyLong())).willReturn(Optional.empty());
        lenient().when(userService.getById(anyLong())).thenReturn(user);

        final Throwable exception = assertThrows(FilmNotFoundException.class, () ->
                                    tempContainer[0] = filmService.deleteLike(firstFilm.getId(), user.getId()));

        verify(storage).findById(firstFilm.getId());
        verify(userService, never()).getById(anyLong());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм с id=%d не найден", firstFilm.getId()));
    }

    @Test
    void givenFilmIdAndUserIdNotPresent_whenDeleteLike_thenThrowUserNotFoundException() {
        given(storage.findById(anyLong())).willReturn(Optional.of(firstFilm));
        given(userService.getById(anyLong())).willThrow(new UserNotFoundException("Пользователь не найден"));

        final Throwable exception = assertThrows(UserNotFoundException.class, () ->
                                      tempContainer[0] = filmService.deleteLike(firstFilm.getId(), user.getId()));

        verify(storage).findById(firstFilm.getId());
        verify(userService).getById(user.getId());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь не найден");
    }

    @Test
    void givenCountPopularFilms_whenGetPopular_thenReturnPopularList() {
        final List<Film> filmList = List.of(firstFilm, secondFilm);
        given(storage.findAll()).willReturn(filmList);

        secondFilm.addLike(user.getId());
        final List<Film> mostPopularSecondFilm = filmService.getPopular(filmList.size());

        verify(storage).findAll();
        assertThat(mostPopularSecondFilm).isNotNull();
        assertThat(mostPopularSecondFilm.size()).isEqualTo(filmList.size());
        assertThat(mostPopularSecondFilm).isEqualTo(List.of(secondFilm, firstFilm));
    }

    @Test
    void givenCountEqualsOnePopularFilm_whenGetPopular_thenReturnPopularListAndHasSizeOne() {
        final List<Film> filmList = List.of(firstFilm, secondFilm);
        given(storage.findAll()).willReturn(filmList);

        secondFilm.addLike(user.getId());
        final List<Film> mostPopularSecondFilm = filmService.getPopular(1);

        verify(storage).findAll();
        assertThat(mostPopularSecondFilm).isNotNull();
        assertThat(mostPopularSecondFilm.size()).isEqualTo(1);
        assertThat(mostPopularSecondFilm).isEqualTo(List.of(secondFilm));
    }

    static void settingsForDefaults() {
        tempContainer[0] = null;

        firstFilm.setId(random.nextInt(32) + 1);
        firstFilm.setName("First film");
        firstFilm.setDescription("Description first");
        firstFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        firstFilm.setDuration(120);
        firstFilm.clearLikes();

        secondFilm.setId(firstFilm.getId() + 1);
        secondFilm.setName("Second film");
        secondFilm.setDescription("Description second");
        secondFilm.setReleaseDate(LocalDate.of(2020, 2, 2));
        secondFilm.setDuration(200);
        secondFilm.clearLikes();

        user.setId(random.nextInt(32) + 1);
        user.setEmail("ivan2000@yandex.ru");
        user.setLogin("Ivan2000");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        user.clearFriendList();
    }

}