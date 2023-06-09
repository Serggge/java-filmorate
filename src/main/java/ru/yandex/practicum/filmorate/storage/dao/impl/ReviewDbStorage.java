package ru.yandex.practicum.filmorate.storage.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.DAOValidator;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static ru.yandex.practicum.filmorate.util.RowMappers.REVIEW_ROW_MAPPER;

@Repository("reviewDbStorage")
@Transactional
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DAOValidator daoValidator;

    public ReviewDbStorage(@Autowired JdbcTemplate jdbcTemplate, DAOValidator daoValidator) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        this.daoValidator = daoValidator;
    }

    @Override
    public Review update(Review review) {
        if (review.getContent() == null) {
            throw new ValidationException("Отзыв не может быть без содержания");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Не указан тип отзыва (Позитив/Негатив)");
        }
        var keyHolder = new GeneratedKeyHolder();
        LocalDateTime reviewDate = LocalDateTime.now();
        String sqlQuery;

        if (review.getReviewId() == 0) {
            sqlQuery = "INSERT INTO review (film_id, user_id, content, isPositive, review_date) " +
                    "VALUES (:film_id, :user_id, :content, :isPositive, :review_date)";
        } else {
            sqlQuery = "UPDATE review SET content = :content, " +
                    "isPositive = :isPositive, review_date = :review_date " +
                    "WHERE review_Id = :review_Id";
        }

        var reviewParams = new MapSqlParameterSource()
                .addValue("review_Id", review.getReviewId())
                .addValue("film_id", review.getFilmId())
                .addValue("user_id", review.getUserId())
                .addValue("content", review.getContent())
                .addValue("isPositive", review.getIsPositive())
                .addValue("useful", review.getUseful())
                .addValue("review_date", reviewDate);

        namedParameterJdbcTemplate.update(sqlQuery, reviewParams, keyHolder);
        if (review.getReviewId() == 0) {
            long autoGeneratedKey = Objects.requireNonNull(keyHolder.getKey()).longValue();
            review.setReviewId(autoGeneratedKey);
        }
        return findById(review.getReviewId());
    }

    @Override
    public Review findById(long id) {
        var sqlQuery = "SELECT review_id, film_id, user_id, content, isPositive, useful, review_date " +
                "FROM REVIEW WHERE REVIEW_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, REVIEW_ROW_MAPPER, id);
    }

    @Override
    public List<Review> findByFilmId(long id, int count) {
        SqlRowSet rows;
        if (id == 0) {
            var sqlQuery = "SELECT review_id, film_id, user_id, content, isPositive, useful, review_date " +
                    "FROM REVIEW ORDER BY USEFUL DESC LIMIT ?";
            rows = jdbcTemplate.queryForRowSet(sqlQuery, count);
        } else {
            daoValidator.validateFilmBd(id);
            var sqlQuery = "SELECT review_id, film_id, user_id, content, isPositive, useful, review_date " +
                    "FROM REVIEW WHERE FILM_ID = ? ORDER BY USEFUL DESC LIMIT ?";
            rows = jdbcTemplate.queryForRowSet(sqlQuery, id, count);
        }
        List<Review> reviewList = new ArrayList<>();
        reviewBuilder(rows, reviewList);
        return reviewList;
    }

    @Override
    public void deleteById(long id) {
        var sqlQuery = "DELETE FROM REVIEW WHERE REVIEW_ID = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void likeReview(Long id, Long userId) {
        Review review = findById(id);
        String sqlQuery = "UPDATE review SET USEFUL = ? WHERE review_Id = ?";
        jdbcTemplate.update(sqlQuery, review.getUseful() + 1, id);
    }

    @Override
    public void dislikeReview(Long id, Long userId) {
        Review review = findById(id);
        String sqlQuery = "UPDATE review SET USEFUL = ? WHERE review_Id = ?";
        jdbcTemplate.update(sqlQuery, review.getUseful() - 1, id);
    }

    private void reviewBuilder(SqlRowSet rows, List<Review> list) {
        while (rows.next()) {
            Review review = new Review(
                    rows.getLong("review_Id"),
                    rows.getLong("film_id"),
                    rows.getLong("user_id"),
                    rows.getString("content"),
                    rows.getBoolean("isPositive"),
                    rows.getInt("useful"),
                    LocalDateTime.parse(Objects.requireNonNull(rows.getString("review_date"))
                            .replace(" ", "T")));
            list.add(review);
        }
    }

    @Override
    public Optional<Long> findUserIdByReviewId(long id) {
        var sqlQuery = "SELECT user_id FROM review WHERE review_Id = ?";
        var rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);
        return rowSet.next() ? Optional.of(rowSet.getLong("user_id")) : Optional.empty();
    }

}
