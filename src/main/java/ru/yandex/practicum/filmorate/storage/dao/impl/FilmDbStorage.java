package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.util.*;
import static ru.yandex.practicum.filmorate.util.RowMappers.FILM_ROW_MAPPER;

@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public FilmDbStorage(@Autowired JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public Film save(Film film) {
        var sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (:name, :description, :releaseDate, :duration, :mpaId)";
        var filmParams = new BeanPropertySqlParameterSource(film);
        var keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sqlQuery, filmParams, keyHolder);
        film.setId(keyHolder.getKey().longValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        var  sqlQuery = "UPDATE films SET name = :name, description = :description, release_date = :releaseDate, " +
                "duration = :duration, mpa_id = :mpaId WHERE film_id = :id";
        var filmParams = new BeanPropertySqlParameterSource(film);
        namedParameterJdbcTemplate.update(sqlQuery, filmParams);
        return film;
    }

    @Override
    public Optional<Film> findById(long id) {
        var sqlQuery = "SELECT film_id, name, description, release_date, duration, mpa_id FROM films WHERE film_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, FILM_ROW_MAPPER, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> findAll() {
        var sqlQuery = "SELECT film_id, name, description, release_date, duration, mpa_id FROM films ORDER BY film_id";
        return jdbcTemplate.query(sqlQuery, FILM_ROW_MAPPER);
    }

    @Override
    public List<Film> findAllById(Collection<Long> ids) {
        var sqlQuery = "SELECT film_id, name, description, release_date, duration, mpa_id " +
                "FROM films WHERE film_id IN (:ids)";
        var idsParams = new MapSqlParameterSource("ids", ids);
        return namedParameterJdbcTemplate.query(sqlQuery, idsParams, FILM_ROW_MAPPER);
    }

    @Override
    public boolean existsById(long id) {
        var sqlQuery = "SELECT film_id FROM films WHERE film_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        return rowSet.next();
    }

    @Override
    public void deleteAll() {
        var sqlQuery = "DELETE FROM films";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public List<Long> findBySubString(String substring) {
        var sqlQuery = "SELECT film_id FROM films WHERE (name ~* :substring) OR (description ~* :substring)";
        var param = new MapSqlParameterSource("substring", substring);
        return namedParameterJdbcTemplate.queryForList(sqlQuery, param, Long.class);
    }

    @Override
    public List<Long> findAllByYear(int year) {
        var sqlQuery = "SELECT film_id FROM films WHERE EXTRACT (YEAR FROM release_date) = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, year);
    }

    @Override
    public List<Long> findAllByGenre(int genreId) {
        var sqlQuery = "SELECT films.film_id FROM films " +
                "INNER JOIN film_genre ON films.film_id = film_genre.film_id WHERE genre_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, genreId);
    }

    @Override
    public List<Long> findAllIds() {
        var sqlQuery = "SELECT film_id FROM films";
        return jdbcTemplate.queryForList(sqlQuery, Long.class);
    }

    private List<Film> mapToFilmList(SqlRowSet rs) {
        List<Film> films = new ArrayList<>();
        Film currentFilm = null;
        while (rs.next()) {
            long filmId = rs.getLong("film_id");
            if (currentFilm == null || currentFilm.getId() != filmId) {
                currentFilm = Film.builder()
                        .id(filmId)
                        .name(rs.getString("name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .duration(rs.getInt("duration"))
                        .mpa(new Mpa(rs.getInt("mpa_id")))
                        .build();
                films.add(currentFilm);
            }
            currentFilm.getGenres().add(new Genre(rs.getInt("genre_id")));
        }
        return films;
    }

    @Override
    public void delete(long filmId) {
        var sqlQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

}
