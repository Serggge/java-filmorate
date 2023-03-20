package ru.yandex.practicum.filmorate.model.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class FilmServiceTest {

    @Mock
    FilmRepository repository;
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
        given(repository.save(any(Film.class))).willReturn(firstFilm);

        Film savedFilm = service.create(firstFilm);

        verify(repository).save(firstFilm);
        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm).isEqualTo(firstFilm);
    }

    @Test
    void givenFilmObject_whenUpdateIncomingFilm_thenReturnFilmObject() {
        given(repository.save(firstFilm)).willReturn(firstFilm);
        given(repository.findById(anyLong())).willReturn(Optional.of(firstFilm));
        given(repository.save(secondFilm)).willReturn(secondFilm);

        Film savedFilm = service.create(firstFilm);
        long id = savedFilm.getId();
        secondFilm.setId(id);
        Film updatedFilm = service.update(secondFilm);

        verify(repository).save(firstFilm);
        verify(repository).save(secondFilm);
        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm).isEqualTo(secondFilm);
    }

    @Test
    void givenFilmList_whenReturnAllFilms_thenReturnFilmList() {
        List<Film> films = List.of(firstFilm, secondFilm);
        given(repository.findAll()).willReturn(films);

        List<Film> allFilms = service.getAll();

        assertThat(allFilms).isNotNull();
        assertThat(allFilms.size()).isEqualTo(films.size());
        assertThat(allFilms).isEqualTo(films);
    }

}