package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film save(Film film) {
        return null;
    }

    @Override
    public Optional<Film> findById(long id) {
        return Optional.empty();
    }

    @Override
    public List<Film> findAll() {
        return null;
    }

    @Override
    public Set<Long> findAllId() {
        return null;
    }
}
