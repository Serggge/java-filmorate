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

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.service.film.FilmServiceImpl;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    FilmStorage storage;
    @InjectMocks
    FilmServiceImpl service;
    static Film firstFilm;
    static Film secondFilm;
    static Random random;
    static Film[] tempContainer;

    @BeforeAll
    static void beforeAll() {
        random = new Random();
        tempContainer = new Film[1];
        firstFilm = new Film();
        secondFilm = new Film();
        setFilmsForDefaults();
    }

    @BeforeEach
    public void beforeEach() {
        setFilmsForDefaults();
        tempContainer[0] = null;
    }

    @Test
    void givenFilmObject_whenAddNewFilm_thenReturnFilmObject() {
        given(storage.save(any(Film.class))).willReturn(firstFilm);

        Film savedFilm = service.create(firstFilm);

        verify(storage).save(firstFilm);
        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm).isEqualTo(firstFilm);
    }

    @Test
    void givenFilmObject_whenUpdateIncomingFilm_thenReturnFilmObject() {
        given(storage.save(firstFilm)).willReturn(firstFilm);
        given(storage.save(secondFilm)).willReturn(secondFilm);

        Film savedFilm = service.create(firstFilm);
        secondFilm.setId(savedFilm.getId());

        given(storage.findAllId()).willReturn(Set.of(savedFilm.getId()));

        Film updatedFilm = service.update(secondFilm);

        verify(storage).save(firstFilm);
        verify(storage).save(secondFilm);
        verify(storage).findAllId();
        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm).isEqualTo(secondFilm);
    }

    @Test
    void givenFilmList_whenReturnAllFilms_thenReturnFilmList() {
        List<Film> films = List.of(firstFilm, secondFilm);
        given(storage.findAll()).willReturn(films);

        List<Film> allFilms = service.getAll();

        verify(storage).findAll();
        assertThat(allFilms).isNotNull();
        assertThat(allFilms.size()).isEqualTo(films.size());
        assertThat(allFilms).isEqualTo(films);
    }

    @Test
    void givenFilmId_whenReturnById_thenReturnFilmObject() {
        given(storage.findById(firstFilm.getId())).willReturn(Optional.of(firstFilm));

        Film returned = service.getById(String.valueOf(firstFilm.getId()));

        verify(storage).findById(firstFilm.getId());
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(firstFilm);
    }

    @Test
    void givenFilmId_whenReturnById_thenThrowNotFoundException() {
        given(storage.findById(firstFilm.getId())).willReturn(Optional.empty());

        Throwable exception = assertThrows(FilmNotFoundException.class, () -> {
           tempContainer[0] = service.getById(String.valueOf(firstFilm.getId()));
        });

        verify(storage).findById(firstFilm.getId());
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм с id=%d не найден", firstFilm.getId()));
        assertThat(tempContainer[0]).isNull();
    }

    @Test
    void givenFilmIdNotNumberType_whenReturnById_thenThrowIncorrectParameterException() {
        lenient().when(storage.findById(anyLong())).thenReturn(Optional.empty());

        IncorrectParameterException exception = assertThrows(IncorrectParameterException.class, () -> {
            tempContainer[0] = service.getById("char");
        });

        verify(storage, never()).findById(anyLong());
        assertThat(tempContainer[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(IncorrectParameterException.class);
        assertThat(exception.getParam()).isEqualTo("id");
        assertThat(exception.getDescription()).isEqualTo("Идентификатор не числовой");
    }


    static void setFilmsForDefaults() {
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
    }

}