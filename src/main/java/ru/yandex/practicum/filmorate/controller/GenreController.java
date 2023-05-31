package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class GenreController {

    private final GenreService service;

    @GetMapping("/{id}")
    public Genre returnById(@PathVariable long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<Genre> returnAll() {
        return service.getAll();
    }

}
