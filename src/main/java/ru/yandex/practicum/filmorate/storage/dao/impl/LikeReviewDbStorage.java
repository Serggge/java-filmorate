package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.LikeReviewStorage;

@Slf4j
@Repository("likeReviewDbStorage")
public class LikeReviewDbStorage implements LikeReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewDbStorage reviewDbStorage;

    public LikeReviewDbStorage(JdbcTemplate jdbcTemplate, ReviewDbStorage reviewDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.reviewDbStorage = reviewDbStorage;
    }

    @Override
    public void likeReview(Long id, Long userId) {
        reviewDbStorage.validateReviewDB(id);
        reviewDbStorage.validateUserBd(userId);

        Review review = reviewDbStorage.findReviewById(id);

        String sqlQuery = "UPDATE review SET USEFUL = ? WHERE review_Id = ?";
        jdbcTemplate.update(sqlQuery, review.getUseful() + 1, id);
    }

    @Override
    public void dislikeReview(Long id, Long userId) {
        reviewDbStorage.validateReviewDB(id);
        reviewDbStorage.validateUserBd(userId);

        Review review = reviewDbStorage.findReviewById(id);

        String sqlQuery = "UPDATE review SET USEFUL = ? WHERE review_Id = ?";
        jdbcTemplate.update(sqlQuery, review.getUseful() - 1, id);
    }
}
