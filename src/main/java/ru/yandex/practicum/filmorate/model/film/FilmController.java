package ru.yandex.practicum.filmorate.model.film;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film add(@RequestBody @Valid Film film) {
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        return filmService.add(film);
    }

    @GetMapping
    public List<Film> list() {
        return filmService.list();
    }

}
