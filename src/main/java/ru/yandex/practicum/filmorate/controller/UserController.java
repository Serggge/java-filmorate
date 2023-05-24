package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class UserController {

    private final UserService userService;
    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addNew(@RequestBody @Valid User user) {
        return userService.create(user);
    }

    @PutMapping
    public User updateExisting(@RequestBody @Valid User user) {
        return userService.update(user);
    }

    @GetMapping
    public List<User> returnAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public User returnById(@PathVariable long id) {
        return userService.getById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User inviteFriend(@PathVariable("id") long userId, @PathVariable long friendId) {
        System.out.println(userId);
        System.out.println(friendId);
        return userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFromFriends(@PathVariable("id") long userId, @PathVariable long friendId) {
        return userService.deleteFriendById(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> returnAllFriends(@PathVariable("id") long userId) {
        return userService.getAllFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> returnMutualFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getMutualFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> recommendFilms(@PathVariable("id") long userId) {
        return filmService.getRecommendedFilms(userId);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable("id") long userId) {
        service.deleteUserById(userId);
    }

    @GetMapping("/{id}/feed")
    public List<Event> returnEvents(@PathVariable("id") long userId) {
        return userService.getEvents(userId);
    }

}
