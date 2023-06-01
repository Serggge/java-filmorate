package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor(onConstructor__ = @Autowired)
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
    public Film returnById(@PathVariable long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addUserLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        service.setLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeUserLike(@PathVariable("id") long filmId, @PathVariable long userId) {
        service.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> returnPopular(@RequestParam Map<String, String> allParams) {
        return service.getPopular(allParams);
    }

    @GetMapping("/search")
    public List<Film> returnFound(@RequestParam String query,
                                  @RequestParam List<String> by) {
        return service.searchByParams(query, by);
    }

    @GetMapping("/common")
    public List<Film> returnCommonPopular(@RequestParam(value = "userId") long userId, @RequestParam(value = "friendId") long friendId) {
        return service.getCommonFilmPopular(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getSortedFilms(@PathVariable("directorId") int directorId, @RequestParam(defaultValue = "year") String sortBy) {
        return service.getSortedFilms(directorId, sortBy);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable("id") long filmId) {
        service.delete(filmId);
    }

}
