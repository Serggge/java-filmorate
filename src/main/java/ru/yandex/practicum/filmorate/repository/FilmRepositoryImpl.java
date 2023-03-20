package ru.yandex.practicum.filmorate.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.*;

@Repository
public class FilmRepositoryImpl implements FilmRepository {

    private final Map<Long, Film> films;

    public FilmRepositoryImpl() {
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

}
