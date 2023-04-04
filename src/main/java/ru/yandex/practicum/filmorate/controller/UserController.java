package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
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

    @GetMapping("/{id}")
    public User returnById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User inviteFriend(@PathVariable String id, @PathVariable String friendId) {
        return service.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFromFriends(@PathVariable String id, @PathVariable String friendId) {
        return service.deleteFriendById(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> returnAllFriends(@PathVariable String id) {
        return service.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> returnMutualFriends(@PathVariable String id, @PathVariable String otherId) {
        return service.getMutualFriends(id, otherId);
    }

}
