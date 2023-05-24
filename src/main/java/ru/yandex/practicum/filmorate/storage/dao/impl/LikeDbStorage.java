package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
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
    public Like save(Like like) {
        var sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (:filmId, :userId)";
        var likeParams = new BeanPropertySqlParameterSource(like);
        namedParameterJdbcTemplate.update(sqlQuery, likeParams);
        return like;
    }

    @Override
    public List<Long> findUsersIdByFilmId(long id) {
        var sqlQuery = "SELECT user_id FROM likes WHERE film_id = ?";
        List<Long> ids = jdbcTemplate.queryForList(sqlQuery, Long.class, id);
        return ids;
    }

    @Override
    @Transactional
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
    public void delete(Like like) {
        var sqlQuery = "DELETE FROM likes WHERE film_id = :filmId AND user_id = :userId";
        var likeParams = new BeanPropertySqlParameterSource(like);
        namedParameterJdbcTemplate.update(sqlQuery, likeParams);
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
        var sqlQuery = "SELECT film_id FROM likes WHERE user_id = ? and film_id in " +
                "(SELECT film_id FROM likes WHERE user_id = ?)";
        return jdbcTemplate.queryForList(sqlQuery, Long.class, userId, friendId);
    }
}
