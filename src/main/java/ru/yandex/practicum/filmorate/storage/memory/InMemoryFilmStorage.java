package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Repository("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
    }

    @Override
    public Film save(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> findById(long id) {
        return films.containsKey(id) ? Optional.of(films.get(id)) : Optional.empty();
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Film> findAllById(Collection<Long> ids) {
        List<Film> result = new ArrayList<>();
        for (Long id : ids) {
            result.add(films.get(id));
        }
        return result;
    }

    @Override
    public boolean existsById(long id) {
        return findById(id).isPresent();
    }

    @Override
    public void deleteAll() {
        films.clear();
    }

    @Override
    public List<Long> findBySubString(String substring) {
        return null;
    }

    @Override
    public List<Long> findAllByYear(int year) {
        return null;
    }

    @Override
    public List<Long> findAllByGenre(int genreId) {
        return null;
    }

    @Override
    public List<Long> findAllIds() {
        return films.values().stream().map(Film::getId).collect(Collectors.toList());
    }

    @Override
    public Film update(Film film) {
        return null;
    }

    @Override
    public void deleteFilm(long id) {
        films.remove(id);
    }
}
