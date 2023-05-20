package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.Validator;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.yandex.practicum.filmorate.util.RowMappers.REVIEW_ROW_MAPPER;

@Slf4j
@Repository("reviewDbStorage")
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ReviewDbStorage(@Autowired JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Override
    public Review create(Review review) {
        if (review.getReviewId() != null) {
            throw new ValidationException("Недопустимый параметр ID при создании ревью");
        }
        // Валидация фильма и пользователя
        validateFilmBd(review.getFilmId());
        validateUserBd(review.getUserId());

        review = save(review, false);
        log.info("Добавлен ревью: {}", review);
        return review;
    }

    @Override
    public Review save(Review review, Boolean isSave) {
        // Проверка на наличие ревью в базе данных, при условии обновления его
        if (isSave) {
            validateReviewDB(review.getReviewId());
        }
        var keyHolder = new GeneratedKeyHolder();
        LocalDateTime reviewDate = LocalDateTime.now();
        String sqlQuery;

        // Валидация фильма и пользователя
        validateFilmBd(review.getFilmId());
        validateUserBd(review.getUserId());

        if (review.getReviewId() == null) {
            sqlQuery = "INSERT INTO review (film_id, user_id, reviewText, reviewDate) " +
                    "VALUES (:film_id, :user_id, :reviewText, :reviewDate)";
        } else {
            sqlQuery = "UPDATE review SET film_id = :film_id, user_id = :user_id, reviewText = :reviewText, reviewDate = :reviewDate " +
                    "WHERE review_Id = :review_Id";
        }

        var reviewParams = new MapSqlParameterSource()
                .addValue("review_Id", review.getReviewId())
                .addValue("film_id", review.getFilmId())
                .addValue("user_id", review.getUserId())
                .addValue("reviewText", review.getReviewText())
                .addValue("reviewDate", reviewDate);

        namedParameterJdbcTemplate.update(sqlQuery, reviewParams, keyHolder);
        if (review.getReviewDate() == null) {
            review.setReviewDate(reviewDate);
        }
        if (review.getReviewId() == null) {
            long autoGeneratedKey = Objects.requireNonNull(keyHolder.getKey()).longValue();
            review.setReviewId(autoGeneratedKey);
        }
        return review;
    }

    @Override
    public Review findReviewById(long id) {
        validateReviewDB(id);
        var sqlQuery = "SELECT * FROM REVIEW WHERE REVIEW_ID = ?";

        return jdbcTemplate.queryForObject(sqlQuery, REVIEW_ROW_MAPPER, id);

    }

    @Override
    public List<Review> findReviewsByFilmId(long id) {
        validateFilmBd(id);
        var sqlQuery = "SELECT * FROM REVIEW WHERE FILM_ID = ?";

        List<Review> reviewList = new ArrayList<>();
        SqlRowSet rows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        while (rows.next()) {
            Review review = new Review(
                    rows.getLong("review_Id"),
                    rows.getLong("film_id"),
                    rows.getLong("user_id"),
                    rows.getString("reviewText"),
                    LocalDateTime.parse(Objects.requireNonNull(rows.getString("reviewDate"))
                            .replace(" ", "T")));
            reviewList.add(review);
        }
        return reviewList;
    }

    @Override
    public void deleteReviewById(long id) {
        validateReviewDB(id);
        var sqlQuery = "DELETE FROM REVIEW WHERE REVIEW_ID = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    private void validateFilmBd(Long id) {
        // Проверка на наличие фильма
        String sqlFilmIdList = "SELECT FILM_ID FROM FILMS";
        List<Long> filmIdList = jdbcTemplate.query(
                sqlFilmIdList, (rs, rowNum) -> rs.getLong("FILM_ID"));
        Validator.validateExistFilm(filmIdList, id);
    }

    private void validateUserBd(Long id) {
        // Проверка на наличие пользователя
        String sqlUserIdList = "SELECT USER_ID FROM USERS";
        List<Long> userIdList = jdbcTemplate.query(
                sqlUserIdList, (rs, rowNum) -> rs.getLong("USER_ID"));
        Validator.validateExistUser(userIdList, id);
    }

    private void validateReviewDB(Long id) {
        // Проверка на наличие ревью
        String sqlReviewIdList = "SELECT REVIEW_ID FROM REVIEW";
        List<Long> reviewIdList = jdbcTemplate.query(
                sqlReviewIdList, (rs, rowNum) -> rs.getLong("REVIEW_ID"));
        Validator.validateExistReview(reviewIdList, id);
    }
}
