package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.dao.FriendStorage;
import java.util.List;
import java.util.Optional;
import static ru.yandex.practicum.filmorate.util.RowMappers.FRIENDSHIP_ROW_MAPPER;

@Repository("friendshipDbStorage")
public class FriendshipDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public Friendship save(Friendship friendship) {
        var sqlQuery = "INSERT INTO friends (user_id, friend_id) VALUES (:userId, :friendId)";
        var friendParams = new BeanPropertySqlParameterSource(friendship);
        namedParameterJdbcTemplate.update(sqlQuery, friendParams);
        return friendship;
    }

    @Override
    public List<Long> findFriendsIdByUserId(long id) {
        var sqlQuery = "SELECT friend_id FROM friends WHERE user_id = :id " +
                        "UNION SELECT user_id FROM friends WHERE friend_id = :id AND confirmed = true";
        var idParam = new MapSqlParameterSource("id", id);
        return namedParameterJdbcTemplate.queryForList(sqlQuery, idParam, Long.class);
    }

    @Override
    public Optional<Friendship> find(Friendship friendship) {
        var sqlQuery = "SELECT user_id, friend_id, confirmed FROM friends " +
                        "WHERE (user_id = :userId AND friend_id = :friendId) " +
                                                  "OR (user_id = :friendId AND friend_id = :userId)";
        var friendParams = new BeanPropertySqlParameterSource(friendship);
        try {
            return Optional.ofNullable(namedParameterJdbcTemplate
                    .queryForObject(sqlQuery, friendParams, FRIENDSHIP_ROW_MAPPER));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void cancel(Friendship friendship) {
        var sqlQuery = "DELETE FROM friends WHERE user_id = :userId AND friend_id = :friendId " +
                                              "OR user_id = :friendId AND friend_id = :userId";
        var friendParams = new BeanPropertySqlParameterSource(friendship);
        namedParameterJdbcTemplate.update(sqlQuery, friendParams);
    }

    @Override
    public boolean isExist(Friendship friendship) {
        var sqlQuery = "SELECT user_id, friend_id FROM friends WHERE (user_id = :userId AND friend_id = :friendId) " +
                "OR (user_id = :friendId AND friend_id = :userId)";
        var idParams = new BeanPropertySqlParameterSource(friendship);
        SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, idParams);
        return rowSet.next();
    }

    @Override
    public boolean isConfirmed(Friendship friendship) {
        var sqlQuery = "SELECT confirmed FROM friends WHERE user_id = :userId AND friend_id = :friendId " +
                                                        "OR user_id = :friendId AND friend_id = :userId";
        var friendParams = new BeanPropertySqlParameterSource(friendship);
        SqlRowSet rs = namedParameterJdbcTemplate.queryForRowSet(sqlQuery, friendParams);
        rs.next();
        return rs.getBoolean("confirmed");
    }

    @Override
    public boolean confirm(Friendship friendship) {
        var sqlQuery = "UPDATE friends SET confirmed = true WHERE (user_id = :userId AND friend_id = :friendId) " +
                                                              "OR (user_id = :friendId AND friend_id = :userId)";
        var friendParams = new BeanPropertySqlParameterSource(friendship);
        int affectedRows = namedParameterJdbcTemplate.update(sqlQuery, friendParams);
        return affectedRows > 0;
    }

    @Override
    public void deleteAll() {
        var sqlQuery = "DELETE FROM friends";
        jdbcTemplate.update(sqlQuery);
    }

}
