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
    public List<Director> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Director getById(@PathVariable int id) {
        return service.getById(id);
    }

    @PostMapping
    public Director create(@RequestBody @Valid Director director) {
        return service.create(director);
    }

    @PutMapping
    public Director update(@RequestBody @Valid Director director) {
        return service.update(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        service.delete(id);
    }

}