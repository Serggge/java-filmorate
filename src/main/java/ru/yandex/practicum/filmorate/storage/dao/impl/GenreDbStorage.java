package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.GenreStorage;
import static ru.yandex.practicum.filmorate.Constants.GENRE_ROW_MAPPER;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository("genreDbStorage")
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Genre> findById(long id) {
        String sqlQuery = "SELECT * FROM genres WHERE genre_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, GENRE_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> findAll() {
        String sqlQuery = "SELECT * FROM genres ORDER BY genre_id";
        try {
            return jdbcTemplate.query(sqlQuery, GENRE_ROW_MAPPER);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean existsById(long id) {
        return findById(id).isPresent();
    }

}
