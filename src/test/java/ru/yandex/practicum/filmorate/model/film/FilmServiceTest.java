package ru.yandex.practicum.filmorate.model.film;

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

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    FilmStorage storage;
    @InjectMocks
    FilmServiceImpl service;
    Film firstFilm;
    Film secondFilm;

    @BeforeEach
    public void beforeEach() {
        firstFilm = Film.builder()
                        .name("First film")
                        .description("Description first")
                        .releaseDate(LocalDate.of(2000, 1, 1))
                        .duration(120)
                        .build();
        secondFilm = Film.builder()
                         .name("Second film")
                         .description("Description second")
                         .releaseDate(LocalDate.of(2020, 2, 2))
                         .duration(200)
                         .build();
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
        given(storage.findById(anyLong())).willReturn(Optional.of(firstFilm));
        given(storage.save(secondFilm)).willReturn(secondFilm);

        Film savedFilm = service.create(firstFilm);
        long id = savedFilm.getId();
        secondFilm.setId(id);
        Film updatedFilm = service.update(secondFilm);

        verify(storage).save(firstFilm);
        verify(storage).save(secondFilm);
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
        final Film film = firstFilm;
        final int randomInt = new Random().nextInt(32) + 1;
        film.setId(randomInt);
        given(storage.findById(randomInt)).willReturn(Optional.of(film));

        Film returned = service.getById(String.valueOf(randomInt));

        verify(storage).findById(randomInt);
        assertThat(returned).isNotNull();
        assertThat(returned).isEqualTo(film);
    }

    @Test
    void givenFilmId_whenReturnById_thenThrowNotFoundException() {
        final int randomInt = new Random().nextInt(32) + 1;
        final Film[] returned = new Film[1];
        given(storage.findById(randomInt)).willReturn(Optional.empty());

        Throwable exception = assertThrows(FilmNotFoundException.class, () -> {
           returned[0] = service.getById(String.valueOf(randomInt));
        });

        verify(storage).findById(randomInt);
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(FilmNotFoundException.class);
        assertThat(exception.getMessage()).isEqualTo(String.format("Фильм с id=%d не найден", randomInt));
        assertThat(returned[0]).isNull();
    }

    @Test
    void givenFilmIdNotNumberType_whenReturnById_thenThrowIncorrectParameterException() {
        final Film[] returned = new Film[1];
        final String id = "s";
        lenient().when(storage.findById(anyLong())).thenReturn(Optional.empty());

        IncorrectParameterException exception = assertThrows(IncorrectParameterException.class, () -> {
            returned[0] = service.getById(id);
        });

        verify(storage, never()).findById(anyLong());
        assertThat(returned[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(IncorrectParameterException.class);
        assertThat(exception.getParam()).isEqualTo("id");
        assertThat(exception.getDescription()).isEqualTo("Идентификатор не числовой");
    }

    @Test
    void givenFilmIdEqualToZero_whenReturnById_thenThrowIncorrectParameterException() {
        final Film[] returned = new Film[1];
        final String id = "0";
        lenient().when(storage.findById(anyLong())).thenReturn(Optional.empty());

        IncorrectParameterException exception = assertThrows(IncorrectParameterException.class, () -> {
           returned[0] = service.getById(id);
        });

        verify(storage, never()).findById(anyLong());
        assertThat(returned[0]).isNull();
        assertThat(exception).isNotNull();
        assertThat(exception.getClass()).isEqualTo(IncorrectParameterException.class);
        assertThat(exception.getParam()).isEqualTo("id");
        assertThat(exception.getMessage()).isEqualTo("Идентификатор должен быть больше 0");
    }

}