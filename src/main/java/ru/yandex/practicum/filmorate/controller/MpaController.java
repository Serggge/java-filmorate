package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService service;

    @Autowired
    public MpaController(MpaService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public Mpa returnById(@PathVariable long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<Mpa> returnAll() {
        return service.getAll();
    }

}
