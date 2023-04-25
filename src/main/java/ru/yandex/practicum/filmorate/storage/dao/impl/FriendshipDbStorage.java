package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.dao.FriendStorage;

import java.util.Collection;
import java.util.Optional;

import static ru.yandex.practicum.filmorate.Constants.FRIENDSHIP_ROW_MAPPER;

@Repository("friendshipDbStorage")
public class FriendshipDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private String sqlQuery;

    @Autowired
    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public Friendship save(Friendship friendship) {
        sqlQuery = "INSERT INTO friends (user_id, friend_id, confirmed) VALUES (:userId, :friendId, :isConfirmed)";
        SqlParameterSource friendParams = new BeanPropertySqlParameterSource(friendship);
        int affectedRows = namedParameterJdbcTemplate.update(sqlQuery, friendParams);
        return friendship;
    }

    @Override
    public Collection<Long> findAllById(long id) {
        sqlQuery = "SELECT DISTINCT friend_id FROM friends WHERE user_id = :id " +
                "UNION SELECT DISTINCT user_id FROM friends WHERE friend_id = :id";
        MapSqlParameterSource idParam = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.queryForList(sqlQuery, idParam, Long.class);
    }

    @Override
    public Optional<Friendship> findById(Friendship friendship) {
        sqlQuery = "SELECT * from friends WHERE user_id = :userId AND friend_id = :friendId " +
                "OR user_id = :friendId AND friend_id = :userId";
        SqlParameterSource friendParams = new BeanPropertySqlParameterSource(friendship);
        return Optional.ofNullable(namedParameterJdbcTemplate
                .queryForObject(sqlQuery, friendParams, FRIENDSHIP_ROW_MAPPER));
    }

    @Override
    public void deleteById(Friendship friendship) {
        sqlQuery = "DELETE FROM friends WHERE user_id = :userId AND friend_id = :friendId " +
                "OR user_id = :friendId AND friend_id = :userId";
        SqlParameterSource friendParams = new BeanPropertySqlParameterSource(friendship);
        int affectedRows = namedParameterJdbcTemplate.update(sqlQuery, friendParams);
    }

    @Override
    public boolean isExist(Friendship friendship) {
        return findById(friendship).isPresent();
    }

}
