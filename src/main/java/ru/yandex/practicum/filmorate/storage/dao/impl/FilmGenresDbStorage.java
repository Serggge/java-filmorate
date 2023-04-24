package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.FilmGenresStorage;
import static ru.yandex.practicum.filmorate.Constants.GENRE_ROW_MAPPER;

@Repository("filmGenresDbStorage")
public class FilmGenresDbStorage implements FilmGenresStorage {

    private final JdbcTemplate jdbcTemplate;
    private String sqlQuery;

    @Autowired
    public FilmGenresDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film save(Film film) {
        sqlQuery = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            film.getGenres().stream()
                    .map(Genre::getId)
                    .forEach(genreId -> jdbcTemplate.update(sqlQuery, film.getId(), genreId));
        return film;
    }

    @Override
    public Iterable<Genre> findAllById(long id) {
        sqlQuery = "SELECT genre_id FROM film_genre WHERE film_id = ?";
        return jdbcTemplate.query(sqlQuery, GENRE_ROW_MAPPER, id);
    }

    @Override
    public void deleteById(long id) {
        sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

}
