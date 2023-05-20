package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
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
        String sqlQuery;
        var keyHolder = new GeneratedKeyHolder();
        if (film.getId() == 0) {
            sqlQuery = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                    "VALUES (:name, :description, :releaseDate, :duration, :mpaId)";
        } else {
            sqlQuery = "UPDATE films SET name = :name, description = :description, release_date = :releaseDate, " +
                    "duration = :duration, mpa_id = :mpaId WHERE film_id = :id";
        }
        var filmParams = new MapSqlParameterSource()
                .addValue("id", film.getId())
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", film.getMpa().getId());
        namedParameterJdbcTemplate.update(sqlQuery, filmParams, keyHolder);
        if (film.getId() == 0) {
            long autoGeneratedKey = Objects.requireNonNull(keyHolder.getKey()).longValue();
            film.setId(autoGeneratedKey);
        }
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
    public List<Long> findByParams(Map<String, String> allParams) {
        String sqlQuery;
        if (allParams.size() == 2) {
            sqlQuery = "SELECT films.film_id AS film_id FROM films " +
                    "INNER JOIN film_genre ON films.film_id = film_genre.film_id " +
                    "WHERE EXTRACT (YEAR FROM release_date) = :year AND genre_id = :genreId ORDER BY film_id";
        } else if (allParams.containsKey("year")) {
            sqlQuery = "SELECT films.film_id AS film_id FROM films " +
                    "INNER JOIN film_genre ON films.film_id = film_genre.film_id " +
                    "WHERE EXTRACT (YEAR FROM release_date) = :year ORDER BY film_id";
        } else if (allParams.containsKey("genreId")) {
            sqlQuery = "SELECT films.film_id AS film_id FROM films " +
                    "INNER JOIN film_genre ON films.film_id = film_genre.film_id " +
                    "WHERE genre_id = :genreId ORDER BY film_id";
        } else {
            sqlQuery = "SELECT DISTINCT film_id FROM films ORDER BY film_id";
        }
        return namedParameterJdbcTemplate.queryForList(sqlQuery, allParams, Long.class);
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
    public List<Long> findAllIds() {
        var sqlQuery = "SELECT DISTINCT film_id FROM films";
        return jdbcTemplate.queryForList(sqlQuery, Long.class);
    }

}
