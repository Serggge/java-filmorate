package ru.yandex.practicum.filmorate.model.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService = new FilmService();

    @PostMapping
    public Film add(@RequestBody @Valid Film film) {
        Film response = filmService.add(film);
        log.info("Добавлен фильм: {}", film.toString());
        return response;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film film) {
        Film response = filmService.update(film);
        log.info("Обновлён фильм: {}", film.toString());
        return response;
    }

    @GetMapping
    public List<Film> list() {
        return filmService.list();
    }

}
