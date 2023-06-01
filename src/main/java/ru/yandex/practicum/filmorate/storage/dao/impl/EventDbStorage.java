package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.dao.EventStorage;
import java.util.List;
import static ru.yandex.practicum.filmorate.util.RowMappers.EVENT_ROW_MAPPER;

@Repository("eventDbStorage")
@Transactional
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public EventDbStorage(@Autowired JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public Event save(Event event) {
        var sqlQuery = "INSERT INTO events (timestamp, user_id, entity_id, event_type, operation) " +
                "VALUES (:timestamp, :userId, :entityId, :eventType, :operation)";
        var keyHolder = new GeneratedKeyHolder();
        var eventParams = new BeanPropertySqlParameterSource(event);
        namedParameterJdbcTemplate.update(sqlQuery, eventParams, keyHolder);
        event.setEventId(keyHolder.getKey().longValue());
        return event;
    }

    @Override
    public List<Event> findAllByUserId(long userId) {
        var sqlQuery = "SELECT event_id, timestamp, user_id, entity_id, event_type, operation " +
                "FROM events WHERE user_id = ? ORDER BY event_id";
        return jdbcTemplate.query(sqlQuery, EVENT_ROW_MAPPER, userId);
    }

    public void deleteAll() {
        var sqlQuery = "DELETE FROM events";
        jdbcTemplate.update(sqlQuery);
    }

}
