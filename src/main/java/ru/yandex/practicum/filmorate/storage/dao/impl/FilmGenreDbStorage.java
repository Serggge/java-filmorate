package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenreStorage;
import java.util.*;
import static ru.yandex.practicum.filmorate.Constants.GENRE_ROW_MAPPER;

@Repository("filmGenresDbStorage")
public class FilmGenreDbStorage implements FilmGenreStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public Film save(Film film) {
        var sqlQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (:film_id, :genre_id)";
        SqlParameterSource[] batch = film.getGenres()
                .stream()
                .map(Genre::getId)
                .map(genreId -> new  MapSqlParameterSource()
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
        var sqlQuery = "SELECT * FROM film_genre WHERE film_id IN (:ids)";
        var idParams = new MapSqlParameterSource("ids", ids);
        List<Map<String, Object>> resultSet = namedParameterJdbcTemplate.queryForList(sqlQuery, idParams);
        Map<Long, Set<Genre>> filmsGenres = new HashMap<>();
        for (var mapRow : resultSet) {
            long filmId = (Integer) mapRow.get("film_id");
            int genreId = (Integer) mapRow.get("genre_id");
            if (!filmsGenres.containsKey(filmId)) {
                filmsGenres.put(filmId, new HashSet<>());
            }
            filmsGenres.get(filmId).add(new Genre(genreId));
        }
        return filmsGenres;
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

}
