package ru.yandex.practicum.filmorate.util;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.*;
import java.sql.ResultSet;
import java.time.LocalDateTime;

public class RowMappers {

    private RowMappers() {

    }

    public static final RowMapper<User> USER_ROW_MAPPER = (ResultSet resultSet, int rowNum) -> User.builder()
            .id(resultSet.getLong("user_id"))
            .login(resultSet.getString("login"))
            .email(resultSet.getString("email"))
            .name(resultSet.getString("name"))
            .birthday(resultSet.getDate("birthday").toLocalDate())
            .build();
    public static final RowMapper<Film> FILM_ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Film.builder()
            .id(resultSet.getLong("film_id"))
            .name(resultSet.getString("name"))
            .description(resultSet.getString("description"))
            .releaseDate(resultSet.getDate("release_date").toLocalDate())
            .duration(resultSet.getInt("duration"))
            .mpa(new Mpa(resultSet.getInt("mpa_id")))
            .build();
    public static final RowMapper<Review> REVIEW_ROW_MAPPER = (ResultSet resultSet, int rowNum) -> Review.builder()
            .reviewId(resultSet.getLong("review_id"))
            .filmId(resultSet.getLong("film_id"))
            .userId(resultSet.getLong("user_id"))
            .content(resultSet.getString("content"))
            .isPositive(resultSet.getBoolean("isPositive"))
            .useful(resultSet.getInt("useful"))
            .reviewDate(resultSet.getObject("reviewDate", LocalDateTime.class))
            .build();
    public static final RowMapper<Mpa> MPA_ROW_MAPPER = (ResultSet resultSet, int rowNum) ->
            new Mpa(resultSet.getInt("mpa_id"));
    public static final RowMapper<Genre> GENRE_ROW_MAPPER = (ResultSet resultSet, int rowNum) ->
            new Genre(resultSet.getInt("genre_id"));
    public static final RowMapper<Like> LIKE_ROW_MAPPER = ((ResultSet resultSet, int rowNum) ->
            new Like(resultSet.getLong("film_id"),
                    resultSet.getLong("user_id")));
    public static final RowMapper<Friendship> FRIENDSHIP_ROW_MAPPER = ((ResultSet resultSet, int rowNum) ->
            new Friendship(resultSet.getLong("user_id"),
                    resultSet.getLong("friend_id")));

}
