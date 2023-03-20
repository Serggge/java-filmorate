package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping
    public ResponseEntity<User> addNew(@RequestBody @Valid User user) {
        return new ResponseEntity<>(service.create(user), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<User> updateExisting(@RequestBody @Valid User user) {
        return ResponseEntity.ok(service.update(user));
    }

    @GetMapping
    public ResponseEntity<List<User>> returnAll() {
        return ResponseEntity.ok(service.getAll());
    }

}
