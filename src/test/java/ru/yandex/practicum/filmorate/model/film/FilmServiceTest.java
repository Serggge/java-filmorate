package ru.yandex.practicum.filmorate.model.film;

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

import ru.yandex.practicum.filmorate.exception.DataUpdateException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.service.film.FilmServiceImpl;
import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
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

        Film savedFilm = filmService.create(firstFilm);

        verify(storage).save(firstFilm);
        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm).isEqualTo(firstFilm);
    }

    @Test
    void givenFilmObject_whenUpdateIncomingFilm_thenReturnFilmObject() {
        given(storage.save(firstFilm)).willReturn(firstFilm);
        given(storage.save(secondFilm)).willReturn(secondFilm);

        Film savedFilm = filmService.create(firstFilm);
        secondFilm.setId(savedFilm.getId());

        given(storage.findAllId()).willReturn(Set.of(savedFilm.getId()));

        Film updatedFilm = filmService.update(secondFilm);

        verify(storage).save(firstFilm);
        verify(storage).save(secondFilm);
        verify(storage).findAllId();
        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm).isEqualTo(secondFilm);
    }

    @Test
    void givenFilmObjectNotPresentInStorage_whenUpdateFilm_thenThrowFilmNotFoundException() {
        given(storage.findAllId()).willReturn(Collections.emptySet());
        lenient().when(storage.save(firstFilm)).thenReturn(firstFilm);

        final Throwable exception = assertThrows(FilmNotFoundException.class, () -> {
           tempContainer[0] = filmService.update(firstFilm);
        });

        verify(storage).findAllId();
        verify(storage, never()).save(any(Film.class));
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм: id=%d не найден", firstFilm.getId()));
    }

    @Test
    void whenGetAllFilms_thenReturnFilmList() {
        List<Film> films = List.of(firstFilm, secondFilm);
        given(storage.findAll()).willReturn(films);

        List<Film> allFilms = filmService.getAll();

        verify(storage).findAll();
        assertThat(allFilms).isNotNull();
        assertThat(allFilms.size()).isEqualTo(films.size());
        assertThat(allFilms).isEqualTo(films);
    }

    @Test
    void givenFilmId_whenGetFilmById_thenReturnFilmObject() {
        given(storage.findById(firstFilm.getId())).willReturn(Optional.of(firstFilm));

        Film returned = filmService.getById(String.valueOf(firstFilm.getId()));

        verify(storage).findById(firstFilm.getId());
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(firstFilm);
    }

    @Test
    void givenFilmIdNotPresentInStorage_whenGetFilmById_thenThrowNotFoundException() {
        given(storage.findById(firstFilm.getId())).willReturn(Optional.empty());

        Throwable exception = assertThrows(FilmNotFoundException.class, () -> {
           tempContainer[0] = filmService.getById(String.valueOf(firstFilm.getId()));
        });

        verify(storage).findById(firstFilm.getId());
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм с id=%d не найден", firstFilm.getId()));
        assertThat(tempContainer[0]).isNull();
    }

    @Test
    void givenFilmIdNotNumberType_whenGetFilmById_thenThrowIncorrectParameterException() {
        lenient().when(storage.findById(anyLong())).thenReturn(Optional.empty());

        IncorrectParameterException exception = assertThrows(IncorrectParameterException.class, () -> {
            tempContainer[0] = filmService.getById("char");
        });

        verify(storage, never()).findById(anyLong());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(IncorrectParameterException.class);
        assertThat(exception.getParam()).isEqualTo("id");
        assertThat(exception.getDescription()).isEqualTo("Идентификатор не числовой");
    }

    @Test
    void givenFilmIdAndUserId_whenSetLike_thenReturnFilmObject() {
        given(storage.findById(anyLong())).willReturn(Optional.of(firstFilm));
        given(userService.getById(anyString())).willReturn(user);

        final Film returned = filmService.setLike(String.valueOf(firstFilm.getId()), String.valueOf(user.getId()));

        verify(storage).findById(firstFilm.getId());
        verify(userService).getById(String.valueOf(user.getId()));
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(firstFilm);
    }

    @Test
    void givenFilmIdNotPresentInStorageAndUserId_whenSetLike_thenThrowFilmNotFoundException() {
        given(storage.findById(firstFilm.getId())).willReturn(Optional.empty());
        lenient().when(userService.getById(anyString())).thenReturn(user);

        final Throwable exception = assertThrows(FilmNotFoundException.class, () -> {
           tempContainer[0] = filmService.setLike(String.valueOf(firstFilm.getId()), String.valueOf(user.getId()));
        });

        verify(storage).findById(firstFilm.getId());
        verify(userService, never()).getById(anyString());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм с id=%d не найден", firstFilm.getId()));
    }

    @Test
    void givenFilmIdAndUserIdNotPresent_whenSetLike_thenThrowUserNotFoundException() {
        given(storage.findById(firstFilm.getId())).willReturn(Optional.of(firstFilm));
        given(userService.getById(anyString())).willThrow(new UserNotFoundException("Пользователь не найден"));

        final Throwable exception = assertThrows(UserNotFoundException.class, () -> {
            tempContainer[0] = filmService.setLike(String.valueOf(firstFilm.getId()), String.valueOf(user.getId()));
        });

        verify(storage).findById(firstFilm.getId());
        verify(userService).getById(String.valueOf(user.getId()));
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(UserNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь не найден");
    }

    @Test
    void givenFilmIdAndUserId_whenDeleteLike_thenReturnFilmObject() {
        given(storage.findById(anyLong())).willReturn(Optional.of(firstFilm));
        given(userService.getById(anyString())).willReturn(user);

        firstFilm.addLike(user.getId());
        final Film returned = filmService.deleteLike(String.valueOf(firstFilm.getId()), String.valueOf(user.getId()));

        verify(storage).findById(firstFilm.getId());
        verify(userService).getById(String.valueOf(user.getId()));
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(firstFilm);
    }

    @Test
    void givenFilmIdAndUserIdWhoNotLikedFilm_whenDeleteLike_thenThrowDataUpdateException() {
        given(storage.findById(anyLong())).willReturn(Optional.of(firstFilm));
        given(userService.getById(anyString())).willReturn(user);

        final Throwable exception = assertThrows(DataUpdateException.class, () -> {
           tempContainer[0] = filmService.deleteLike(String.valueOf(firstFilm.getId()), String.valueOf(user.getId()));
        });

        verify(storage).findById(firstFilm.getId());
        verify(userService).getById(String.valueOf(user.getId()));
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(DataUpdateException.class);
        assertThat(exception.getMessage()).isEqualTo("Пользователь ранее не оставлял лайк");
    }

    @Test
    void givenFilmIdNotPresentInStorageAndUserId_whenDeleteLike_thenThrowFilmNotFoundException() {
        given(storage.findById(firstFilm.getId())).willReturn(Optional.empty());
        lenient().when(userService.getById(anyString())).thenReturn(user);

        final Throwable exception = assertThrows(FilmNotFoundException.class, () -> {
            tempContainer[0] = filmService.deleteLike(String.valueOf(firstFilm.getId()), String.valueOf(user.getId()));
        });

        verify(storage).findById(firstFilm.getId());
        verify(userService, never()).getById(anyString());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм с id=%d не найден", firstFilm.getId()));
    }

    @Test
    void givenFilmIdAndUserIdNotPresent_whenDeleteLike_thenThrowUserNotFoundException() {
        given(storage.findById(firstFilm.getId())).willReturn(Optional.of(firstFilm));
        given(userService.getById(anyString())).willThrow(new UserNotFoundException("Пользователь не найден"));

        final Throwable exception = assertThrows(UserNotFoundException.class, () -> {
            tempContainer[0] = filmService.deleteLike(String.valueOf(firstFilm.getId()), String.valueOf(user.getId()));
        });

        verify(storage).findById(firstFilm.getId());
        verify(userService).getById(String.valueOf(user.getId()));
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
        final List<Film> mostPopularSecondFilm = filmService.getPopular(String.valueOf(filmList.size()));

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
        final List<Film> mostPopularSecondFilm = filmService.getPopular(String.valueOf(1));

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