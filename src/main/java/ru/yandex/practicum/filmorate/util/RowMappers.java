package ru.yandex.practicum.filmorate.util;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.*;
import java.sql.ResultSet;
import java.time.Instant;

public class RowMappers {

    private RowMappers() {

    }

    public static final RowMapper<User> USER_ROW_MAPPER = (ResultSet rs, int rowNum) -> User.builder()
            .id(rs.getLong("user_id"))
            .login(rs.getString("login"))
            .email(rs.getString("email"))
            .name(rs.getString("name"))
            .birthday(rs.getDate("birthday").toLocalDate())
            .build();
    public static final RowMapper<Film> FILM_ROW_MAPPER = (ResultSet rs, int rowNum) -> Film.builder()
            .id(rs.getLong("film_id"))
            .name(rs.getString("name"))
            .description(rs.getString("description"))
            .releaseDate(rs.getDate("release_date").toLocalDate())
            .duration(rs.getInt("duration"))
            .mpa(new Mpa(rs.getInt("mpa_id")))
            .build();
    public static final RowMapper<Mpa> MPA_ROW_MAPPER = (ResultSet rs, int rowNum) ->
            new Mpa(rs.getInt("mpa_id"));
    public static final RowMapper<Genre> GENRE_ROW_MAPPER = (ResultSet rs, int rowNum) ->
            new Genre(rs.getInt("genre_id"));
    public static final RowMapper<Like> LIKE_ROW_MAPPER = ((ResultSet rs, int rowNum) ->
            new Like(rs.getLong("film_id"),
                    rs.getLong("user_id")));
    public static final RowMapper<Friendship> FRIENDSHIP_ROW_MAPPER = ((ResultSet rs, int rowNum) ->
            new Friendship(rs.getLong("user_id"),
                    rs.getLong("friend_id")));
    public static final RowMapper<Event> EVENT_ROW_MAPPER = ((ResultSet rs, int rowNum) ->
            Event.builder().eventId(rs.getLong("event_id"))
                    .timestamp(Instant.ofEpochMilli(rs.getLong("timestamp")))
                    .userId(rs.getLong("user_id"))
                    .entityId(rs.getLong("entity_id"))
                    .eventType(EventType.valueOf(rs.getString("event_type")))
                    .operation(Operation.valueOf(rs.getString("operation")))
                    .build());

}
