package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    @Autowired
    private FilmService filmService;

    @PostMapping
    public ResponseEntity<Film> addNew(@RequestBody @Valid Film film) {
        return new ResponseEntity<>(filmService.create(film), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateExisting(@RequestBody @Valid Film film) {
        return ResponseEntity.ok(filmService.update(film));
    }

    @GetMapping
    public ResponseEntity<List<Film>> returnAll() {
        return ResponseEntity.ok(filmService.getAll());
    }

}
