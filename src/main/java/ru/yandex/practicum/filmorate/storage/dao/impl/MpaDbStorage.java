package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;
import java.util.List;
import java.util.Optional;
import static ru.yandex.practicum.filmorate.util.RowMappers.MPA_ROW_MAPPER;

@Repository("mpaDbStorage")
@Transactional
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Mpa> findById(long id) {
        String sqlQuery = "SELECT mpa_id, name FROM mpa WHERE mpa_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, MPA_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Mpa> findAll() {
        var sqlQuery = "SELECT mpa_id, name FROM mpa ORDER BY mpa_id";
        return jdbcTemplate.query(sqlQuery, MPA_ROW_MAPPER);
    }

    @Override
    public boolean existsById(long id) {
        var sqlQuery = "SELECT mpa_id FROM mpa WHERE mpa_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        return rowSet.next();
    }

}
