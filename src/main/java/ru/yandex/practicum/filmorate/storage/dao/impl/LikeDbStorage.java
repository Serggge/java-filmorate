package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;
import java.util.Collection;
import java.util.List;

import static ru.yandex.practicum.filmorate.Constants.LIKE_ROW_MAPPER;

@Repository("likeDbStorage")
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public Like save(Like like) {
        var sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (:filmId, :userId)";
        SqlParameterSource likeParams = new BeanPropertySqlParameterSource(like);
        int affectedRows = namedParameterJdbcTemplate.update(sqlQuery, likeParams);
        return like;
    }

    @Override
    public Collection<Long> findAllById(long id) {
        var sqlQuery = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> ids = jdbcTemplate.queryForList(sqlQuery, Long.class, id);
        return ids;
    }

    @Override
    public void deleteById(Like like) {
        var sqlQuery = "DELETE FROM likes WHERE film_id = :filmId AND user_id = :userId";
        SqlParameterSource likeParams = new BeanPropertySqlParameterSource(like);
        namedParameterJdbcTemplate.update(sqlQuery, likeParams);
    }

    public List<Long> getPopular(int size) {
        var sqlQuery = "SELECT film_id FROM likes GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?";
        List<Long> ids = jdbcTemplate.queryForList(sqlQuery, Long.class, size);
        return ids;
    }

    @Override
    public boolean isExist(Like like) {
        var sqlQuery = "SELECT * FROM likes WHERE film_id = :filmId AND user_id = :userId";
        SqlParameterSource likeParams = new BeanPropertySqlParameterSource(like);
        return namedParameterJdbcTemplate.query(sqlQuery, likeParams, LIKE_ROW_MAPPER).size() == 1;
    }

}
