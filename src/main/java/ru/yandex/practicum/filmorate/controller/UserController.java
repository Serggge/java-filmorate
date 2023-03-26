package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addNew(@RequestBody @Valid User user) {
        return service.create(user);
    }

    @PutMapping
    public User updateExisting(@RequestBody @Valid User user) {
        return service.update(user);
    }

    @GetMapping
    public List<User> returnAll() {
        return service.getAll();
    }

}
