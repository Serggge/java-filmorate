package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataBaseResponseException;
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
        sqlQuery = "INSERT INTO friends (user_id, friend_id) VALUES (:userId, :friendId)";
        SqlParameterSource friendParams = new BeanPropertySqlParameterSource(friendship);
        int affectedRows = namedParameterJdbcTemplate.update(sqlQuery, friendParams);
        return friendship;
    }

    @Override
    public Collection<Long> findAllById(long id) {
        sqlQuery = "SELECT friend_id FROM friends WHERE user_id = :id " +
                " UNION SELECT user_id FROM friends WHERE friend_id = :id AND confirmed = true";
        MapSqlParameterSource idParam = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.queryForList(sqlQuery, idParam, Long.class);
    }

    @Override
    public Optional<Friendship> findById(Friendship friendship) {
        sqlQuery = "SELECT * from friends WHERE user_id = :userId AND friend_id = :friendId " +
                "OR user_id = :friendId AND friend_id = :userId";
        SqlParameterSource friendParams = new BeanPropertySqlParameterSource(friendship);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate
                    .queryForObject(sqlQuery, friendParams, FRIENDSHIP_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
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

    @Override
    public boolean isConfirmed(Friendship friendship) {
        sqlQuery = "SELECT confirmed FROM friends WHERE user_id = :userId AND friend_id = :friendId " +
                "OR user_id = :friendId AND friend_id = :userId";
        SqlParameterSource friendParams = new BeanPropertySqlParameterSource(friendship);
        SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, friendParams);
        boolean isConfirmed = false;
        if (rs.next()) {
            isConfirmed = rs.getBoolean("confirmed");
        } else {
            throw new DataBaseResponseException("Пользователи не являются друзьями");
        }
        return isConfirmed;
    }

    @Override
    public boolean confirm(Friendship friendship) {
        sqlQuery = "UPDATE friends SET confirmed = true WHERE user_id = :userId AND friend_id = :friendId " +
                "OR user_id = :friendId AND friend_id = :userId";
        SqlParameterSource friendParams = new BeanPropertySqlParameterSource(friendship);
        int affectedRows = namedParameterJdbcTemplate.update(sqlQuery, friendParams);
        return affectedRows > 0;
    }

}
