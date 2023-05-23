package ru.yandex.practicum.filmorate.storage.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.LikeReviewStorage;
import ru.yandex.practicum.filmorate.storage.dao.DAOValidator;

@Slf4j
@Repository("likeReviewDbStorage")
public class LikeReviewDbStorage implements LikeReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewService reviewService;
    private final DAOValidator daoValidator;

    public LikeReviewDbStorage(JdbcTemplate jdbcTemplate, ReviewService reviewService, DAOValidator daoValidator) {
        this.jdbcTemplate = jdbcTemplate;
        this.reviewService = reviewService;
        this.daoValidator = daoValidator;
    }

    @Override
    public void likeReview(Long id, Long userId) {
        daoValidator.validateReviewDB(id);
        daoValidator.validateUserBd(userId);

        Review review = reviewService.findById(id);

        String sqlQuery = "UPDATE review SET USEFUL = ? WHERE review_Id = ?";
        jdbcTemplate.update(sqlQuery, review.getUseful() + 1, id);
    }

    @Override
    public void dislikeReview(Long id, Long userId) {
        daoValidator.validateReviewDB(id);
        daoValidator.validateUserBd(userId);

        Review review = reviewService.findById(id);

        String sqlQuery = "UPDATE review SET USEFUL = ? WHERE review_Id = ?";
        jdbcTemplate.update(sqlQuery, review.getUseful() - 1, id);
    }
}
