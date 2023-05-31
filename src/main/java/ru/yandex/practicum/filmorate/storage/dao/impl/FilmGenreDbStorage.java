package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;
import java.util.*;
import java.util.stream.Collectors;
import static ru.yandex.practicum.filmorate.util.RowMappers.GENRE_ROW_MAPPER;

@Repository("filmGenresDbStorage")
@Transactional
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public FilmGenreDbStorage(@Autowired JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public Film save(Film film) {
        var sqlQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (:film_id, :genre_id)";
        SqlParameterSource[] batch = film.getGenres()
                .stream()
                .map(Genre::getId)
                .map(genreId -> new MapSqlParameterSource()
                        .addValue("film_id", film.getId())
                        .addValue("genre_id", genreId))
                .toArray(SqlParameterSource[]::new);
        namedParameterJdbcTemplate.batchUpdate(sqlQuery, batch);
        return film;
    }

    @Override
    public List<Genre> findGenresByFilmId(long id) {
        var sqlQuery = "SELECT genre_id FROM film_genre WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, GENRE_ROW_MAPPER, id);
    }

    @Override
    public Map<Long, Set<Genre>> findAll(Collection<Long> ids) {
        var sqlQuery = "SELECT film_id, genre_id FROM film_genre WHERE film_id IN (:ids)";
        var idParams = new MapSqlParameterSource("ids", ids);
        return namedParameterJdbcTemplate.queryForStream(sqlQuery, idParams, (rs, rowNum) ->
                        Map.entry(rs.getLong("film_id"), new Genre(rs.getInt("genre_id"))))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(
                                Map.Entry::getValue,
                                Collectors.toSet()
                        )));
    }

    @Override
    public void deleteByFilmId(long id) {
        var sqlQuery = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void deleteAll() {
        var sqlQuery = "DELETE from film_genre";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public List<Long> findAllByGenre(int genreId) {
        var sqlQuery = "SELECT film_id from film_genre WHERE genre_id = ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, genreId);
    }

}
