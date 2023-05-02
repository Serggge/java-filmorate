package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;
import java.util.*;
import static ru.yandex.practicum.filmorate.util.RowMappers.LIKE_ROW_MAPPER;

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
    public Map<Long, Set<Long>> findAll(Collection<Long> ids) {
        var sqlQuery = "SELECT film_id, user_id FROM likes WHERE film_id IN (:ids)";
        var idParams = new MapSqlParameterSource("ids", ids);
        List<Map<String, Object>> resultSet = namedParameterJdbcTemplate.queryForList(sqlQuery, idParams);
        Map<Long, Set<Long>> filmsLikes = new HashMap<>();
        for (var mapRow : resultSet) {
            long filmId = (Integer) mapRow.get("film_id");
            long userId = (Integer) mapRow.get("user_id");
            if (!filmsLikes.containsKey(filmId)) {
                filmsLikes.put(filmId, new HashSet<>());
            }
            filmsLikes.get(filmId).add(userId);
        }
        return filmsLikes;
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
        SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, likeParams);
        return rowSet.next();
    }

    @Override
    public void deleteAll() {
        var sqlQuery = "DELETE FROM likes";
        jdbcTemplate.update(sqlQuery);
    }
}
