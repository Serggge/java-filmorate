package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataException;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.DirectorsStorage;

import java.util.*;

@Repository
public class DirectorsStorageImpl implements DirectorsStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DirectorsStorageImpl(@Autowired JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public List<Director> getAllDirectors() {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("SELECT * FROM directors");
        List<Director> directors = new ArrayList<>();
        while (rows.next()) {
            Director director = Director.builder()
                    .id(rows.getInt("id"))
                    .name(rows.getString("name"))
                    .build();
            directors.add(director);
        }
        return directors;
    }

    @Override
    public Director getDirectorById(int id) {
        SqlRowSet rows = jdbcTemplate.queryForRowSet("SELECT * FROM directors WHERE id = ?", id);
        if (rows.next()) {
            return Director.builder()
                    .id(rows.getInt("id"))
                    .name(rows.getString("name"))
                    .build();
        } else {
            throw new DirectorNotFoundException("Такой режиссер не найден: " + id);
        }
    }

    @Override
    public Director createDirector(Director director) {
        String sql = "SELECT * FROM directors WHERE id = ?;";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, director.getId());
        if (row.next()) {
            throw new DataException("Режиссер  уже существует" + director.getId());
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("id");
        director.setId(simpleJdbcInsert.executeAndReturnKey(getValue(director)).intValue());
        return director;

    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "SELECT * FROM directors WHERE id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, director.getId());
        if (rows.next()) {
            sql = "UPDATE directors SET name = ? WHERE id = ?;";
            jdbcTemplate.update(sql, director.getName(), director.getId());
            return director;
        } else {
            throw new DirectorNotFoundException("Такой режиссер не найден: " + director.getId());
        }
    }

    @Override
    public void deleteDirector(int id) {
        String sqlQuery = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film save(Film film) {
        var sql = "INSERT INTO film_directors (film_id, director_id) VALUES (:film_id, :director_id)";
        SqlParameterSource[] batch = film.getDirectors()
                .stream()
                .map(Director::getId)
                .map(directorId -> new MapSqlParameterSource()
                        .addValue("film_id", film.getId())
                        .addValue("director_id", directorId))
                .toArray(SqlParameterSource[]::new);
        namedParameterJdbcTemplate.batchUpdate(sql, batch);
        return film;
    }

    @Override
    public List<Director> findDirectorsByFilmId(long id) {
        List<Director> directors = new ArrayList<>();
        String sql = "SELECT director_id FROM film_directors WHERE film_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        while (rows.next()) {
            sql = "SELECT * FROM directors WHERE id = ?";
            SqlRowSet rowDirector = jdbcTemplate.queryForRowSet(sql, rows.getInt("director_id"));
            if (rowDirector.next()) {
                directors.add(new Director(rowDirector.getInt("id"), rowDirector.getString("name")));
            }
        }
        return directors;
    }

    @Override
    public Map<Long, Set<Director>> findAll(Collection<Long> ids) {
        Map<Long, Set<Director>> directorsMap = new HashMap<>();
        for (Long id : ids) {
            Set<Director> directors = new HashSet<>();
            String sql = "SELECT director_id FROM film_directors WHERE film_id = ?";
            SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
            while (rows.next()) {
                sql = "SELECT * FROM directors WHERE id = ?";
                SqlRowSet rowDirector = jdbcTemplate.queryForRowSet(sql, rows.getInt("director_id"));
                if (rowDirector.next()) {
                    directors.add(new Director(rowDirector.getInt("id"), rowDirector.getString("name")));
                }
            }
            directorsMap.put(id, directors);
        }
        return directorsMap;
    }

    @Override
    public void deleteByFilmId(long id) {
        String sqlQuery = "DELETE FROM film_directors WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    public Map<String, Object> getValue(Director director) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", director.getName());
        return values;
    }

}
