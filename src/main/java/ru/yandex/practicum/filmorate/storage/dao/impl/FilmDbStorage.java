package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.util.*;
import static ru.yandex.practicum.filmorate.util.Statements.STATEMENT_FOR_FILM;

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
        var sqlQuery = "UPDATE films SET name = :name, description = :description, release_date = :releaseDate, " +
                "duration = :duration, mpa_id = :mpaId WHERE film_id = :id";
        var filmParams = new BeanPropertySqlParameterSource(film);
        namedParameterJdbcTemplate.update(sqlQuery, filmParams);
        return film;
    }

    @Override
    public Optional<Film> findById(long id) {
        var sqlQuery = STATEMENT_FOR_FILM + " WHERE films.film_id = ?";
        var rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        Film film = mapToFilm(rowSet);
        return Optional.ofNullable(film);
    }

    @Override
    public List<Film> findAll() {
        var rowSet = jdbcTemplate.queryForRowSet(STATEMENT_FOR_FILM);
        return mapToFilmList(rowSet);
    }

    @Override
    public List<Film> findAllById(Collection<Long> ids) {
        var sqlQuery = STATEMENT_FOR_FILM + " WHERE films.film_id IN (:ids)";
        var idsParams = new MapSqlParameterSource("ids", ids);
        var rowSet = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, idsParams);
        return mapToFilmList(rowSet);
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
        var sqlQuery = "SELECT film_id FROM films WHERE (name ~* :substring)";
        var param = new MapSqlParameterSource("substring", substring);
        return namedParameterJdbcTemplate.queryForList(sqlQuery, param, Long.class);
    }

    @Override
    public List<Long> findAllByYear(int year) {
        var sqlQuery = "SELECT film_id FROM films WHERE EXTRACT(YEAR FROM release_date) = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, year);
    }

    @Override
    public List<Long> findAllIds() {
        var sqlQuery = "SELECT film_id FROM films";
        return jdbcTemplate.queryForList(sqlQuery, Long.class);
    }

    @Override
    public void delete(long filmId) {
        var sqlQuery = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public List<Long> findPopular(int count) {
        var sqlQuery = "SELECT films.film_id FROM films LEFT JOIN likes ON likes.film_id = films.film_id " +
                "GROUP BY films.film_id ORDER BY COUNT(likes.user_id) DESC LIMIT ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, count);
    }

    @Override
    public List<Long> findByYearAndGenre(int year, int genreId) {
        var sqlQuery = "SELECT films.film_id FROM films INNER JOIN film_genre ON film_genre.film_id = films.film_id " +
                "WHERE EXTRACT(YEAR FROM films.release_date) = :year AND film_genre.genre_id = :genreId";
        var paramsSource = new MapSqlParameterSource()
                .addValue("year", year)
                .addValue("genreId", genreId);
        return namedParameterJdbcTemplate.queryForList(sqlQuery, paramsSource, Long.class);
    }

    private List<Film> mapToFilmList(SqlRowSet rs) {
        List<Film> films = new ArrayList<>();
        Film currentFilm = null;
        while (rs.next()) {
            rs.previous();
            currentFilm = mapToFilm(rs);
            films.add(currentFilm);
        }
        return  films;
    }

    private Film mapToFilm(SqlRowSet rs) {
        Film film = null;
        if (rs.next()) {
            film = Film.builder()
                    .id(rs.getLong("film_id"))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(rs.getInt("duration"))
                    .mpa(new Mpa(rs.getInt("mpa_id")))
                    .build();
            rs.previous();
        } else {
            return null;
        }
        while (rs.next() && film.getId() == rs.getLong("film_id")) {
            int genreId = rs.getInt("GENRE_ID");
            if (!rs.wasNull()) {
                film.addGenre(new Genre(genreId));
            }
            int directorId = rs.getInt("DIRECTOR_ID");
            if (!rs.wasNull()) {
                String directorName = rs.getString("DIRECTOR_NAME");
                film.addDirector(new Director(directorId, directorName));
            }
            long userId = rs.getLong("USER_ID");
            if (!rs.wasNull()) {
                film.addLike(userId);
            }
        }
        rs.previous();
        return film;
    }

}
