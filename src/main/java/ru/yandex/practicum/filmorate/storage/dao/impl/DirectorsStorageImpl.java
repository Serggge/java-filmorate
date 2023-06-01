package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.DataException;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.DirectorsStorage;
import java.util.*;
import java.util.stream.Collectors;
import static ru.yandex.practicum.filmorate.util.RowMappers.DIRECTOR_ROW_MAPPER;

@Repository
@Transactional
public class DirectorsStorageImpl implements DirectorsStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DirectorsStorageImpl(@Autowired JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public List<Director> getAll() {
        var sqlQuery = "SELECT id, name FROM directors";
        return jdbcTemplate.query(sqlQuery, DIRECTOR_ROW_MAPPER);
    }

    @Override
    public Director getById(int id) {
        var sqlQuery = "SELECT id, name FROM directors WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, DIRECTOR_ROW_MAPPER, id);
        } catch (EmptyResultDataAccessException e) {
            throw new DirectorNotFoundException("Такой режиссер не найден: " + id);
        }
    }

    @Override
    public Director create(Director director) {
        String sql = "SELECT id FROM directors WHERE id = ?;";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, director.getId());
        if (row.next()) {
            throw new DataException("Режиссер  уже существует" + director.getId());
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("id");
        director.setId(simpleJdbcInsert.executeAndReturnKey(buildDirector(director)).intValue());
        return director;

    }

    @Override
    public Director update(Director director) {
        String sql = "SELECT id FROM directors WHERE id = ?";
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
    public void delete(int id) {
        String sqlQuery = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film save(Film film) {
        String sql = "INSERT INTO film_directors (film_id, director_id) VALUES (:film_id, :director_id)";
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
        List<Integer> directorsId = new ArrayList<>();
        String sql = "SELECT director_id FROM film_directors WHERE film_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, id);
        while (rows.next()) {
            directorsId.add(rows.getInt("director_id"));
        }
        SqlParameterSource parameters = new MapSqlParameterSource("ids", directorsId);
        return namedParameterJdbcTemplate.queryForStream(
                "SELECT id, name FROM directors WHERE id IN (:ids)",
                parameters,
                (rs, rowNum) -> new Director(rs.getInt("id"), rs.getString("name"))).collect(Collectors.toList());
    }

    @Override
    public Map<Long, Set<Director>> findAll(Collection<Long> ids) {
        String sql = "SELECT film_id, director_id, name FROM film_directors " +
                "INNER JOIN directors on film_directors.director_id = directors.id WHERE film_id IN (:ids)";
        var idParams = new MapSqlParameterSource("ids", ids);
        return namedParameterJdbcTemplate.queryForStream(sql, idParams, (rs, rowNum) ->
                        Map.entry(rs.getLong("film_id"),
                                new Director(rs.getInt("director_id"), rs.getString("name"))))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(
                                Map.Entry::getValue,
                                Collectors.toSet()
                        )));
    }

    @Override
    public void deleteByFilmId(long id) {
        String sqlQuery = "DELETE FROM film_directors WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public List<Long> findBySubString(String substring) {
        var sqlQuery = "SELECT DISTINCT film_id FROM film_directors " +
                "INNER JOIN directors ON directors.id = film_directors.director_id " +
                "WHERE name ~* ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, substring);
    }

    @Override
    public boolean existsById(int directorId) {
        var sqlQuery = "SELECT id FROM directors WHERE id = ?";
        var rowSet = jdbcTemplate.queryForRowSet(sqlQuery, directorId);
        return rowSet.next();
    }

    public Map<String, Object> buildDirector(Director director) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", director.getName());
        return values;
    }

    public List<Long> getSortedFilms(int directorId) {
        List<Long> ids = new ArrayList<>();
        String sql = "SELECT film_id FROM film_directors WHERE director_id = ?";
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, directorId);
        while (rows.next()) {
            ids.add((long) rows.getInt("film_id"));
        }
        return ids;
    }

    public void deleteAll() {
        var sqlQuery = "DELETE FROM directors";
        jdbcTemplate.update(sqlQuery);
    }

}
