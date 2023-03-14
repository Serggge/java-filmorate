package ru.yandex.practicum.filmorate.model.film;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film add(@RequestBody @Valid Film film) {
        try {
            return filmService.add(film);
        } finally {
            log.info("Добавлен фильм: {}", film.toString());
        }
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        try {
            return filmService.update(film);
        } finally {
            log.info("Обновлён фильм: {}", film.toString());
        }
    }

    @GetMapping
    public List<Film> list() {
        return filmService.list();
    }

}
