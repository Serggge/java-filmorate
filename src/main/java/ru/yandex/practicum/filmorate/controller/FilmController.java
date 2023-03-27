package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    private final FilmService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film addNew(@RequestBody @Valid Film film) {
        return service.create(film);
    }

    @PutMapping
    public Film updateExisting(@RequestBody @Valid Film film) {
        return service.update(film);
    }

    @GetMapping
    public List<Film> returnAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Film returnById(@PathVariable String id) {
        return service.getById(id);
    }

}
