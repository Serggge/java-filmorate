package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;
import java.util.*;
import java.util.stream.Collectors;
import static ru.yandex.practicum.filmorate.util.RowMappers.LIKE_ROW_MAPPER;

@Repository("likeDbStorage")
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public LikeDbStorage(@Autowired JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public boolean save(Like like) {
        var sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (:filmId, :userId)";
        var likeParams = new BeanPropertySqlParameterSource(like);
        return namedParameterJdbcTemplate.update(sqlQuery, likeParams) > 0;
    }

    @Override
    public List<Long> findUsersIdByFilmId(long id) {
        var sqlQuery = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> ids = jdbcTemplate.queryForList(sqlQuery, Long.class, id);
        return ids;
    }

    @Override
    public Map<Long, Set<Long>> findAll(Collection<Long> ids) {
        var sqlQuery = "SELECT film_id, user_id FROM likes WHERE film_id IN (:ids)";
        var idParams = new MapSqlParameterSource("ids", ids);
        return namedParameterJdbcTemplate.queryForStream(sqlQuery, idParams, LIKE_ROW_MAPPER)
                .collect(Collectors.groupingBy(
                        Like::getFilmId,
                        Collectors.mapping(
                                Like::getUserId,
                                Collectors.toSet())));
    }

    @Override
    public boolean delete(Like like) {
        var sqlQuery = "DELETE FROM likes WHERE film_id = :filmId AND user_id = :userId";
        var likeParams = new BeanPropertySqlParameterSource(like);
        return namedParameterJdbcTemplate.update(sqlQuery, likeParams) > 0;
    }

    @Override
    public boolean isExist(Like like) {
        var sqlQuery = "SELECT film_id, user_id FROM likes WHERE film_id = :filmId AND user_id = :userId";
        var likeParams = new BeanPropertySqlParameterSource(like);
        var rowSet = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, likeParams);
        return rowSet.next();
    }

    @Override
    public void deleteAll() {
        var sqlQuery = "DELETE FROM likes";
        jdbcTemplate.update(sqlQuery);
    }

    @Override
    public List<Long> findCommonLikes(long userId, long friendId) {
        var sqlQuery = "SELECT film_id FROM likes WHERE user_id=? OR user_id=? " +
                "GROUP BY film_id HAVING count(user_id)=2";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, userId, friendId);
    }

    @Override
    public List<Long> findPopular(int count) {
        var sqlQuery = "SELECT film_id FROM likes GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, count);
    }

    @Override
    public List<Long> suggestFilms(long userId) {
        var sqlQuery = "WITH user_favorite_films AS " +
                "    (SELECT film_id " +
                "     FROM likes " +
                "     WHERE user_id = :userId) " +
                "SELECT DISTINCT film_id " +
                "FROM likes " +
                "WHERE user_id IN " +
                "        (SELECT user_id " +
                "         FROM likes " +
                "         WHERE film_id IN " +
                "                 (SELECT film_id " +
                "                  FROM user_favorite_films) " +
                "             AND user_id != :userId " +
                "         GROUP BY user_id " +
                "         ORDER BY COUNT(user_id) DESC " +
                "         LIMIT 3) " +
                "    AND film_id NOT IN " +
                "        (SELECT film_id " +
                "         FROM user_favorite_films)";
        return namedParameterJdbcTemplate.queryForList(sqlQuery,
                new MapSqlParameterSource("userId", userId), Long.class);
    }

}
