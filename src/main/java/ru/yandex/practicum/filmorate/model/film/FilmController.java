package ru.yandex.practicum.filmorate.model.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    @Autowired
    private FilmService filmService;

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        Film response = filmService.addFilm(film);
        log.info("Добавлен фильм: {}", film.toString());
        return response;
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        Film response = filmService.updateFilm(film);
        log.info("Обновлён фильм: {}", film.toString());
        return response;
    }

    @GetMapping
    public List<Film> returnAllFilms() {
        return filmService.returnAllFilms();
    }

}
