package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorsService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class DirectorsController {

    private final DirectorsService service;

    @GetMapping
    public List<Director> getAllDirectors() {
        return service.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable int id) {
        return service.getDirectorById(id);
    }

    @PostMapping
    public Director createDirector(@RequestBody @Valid Director director) {
        return service.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@RequestBody @Valid Director director) {
        return service.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
        service.deleteDirector(id);
    }

}