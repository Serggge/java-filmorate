package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.service.Validator;
import java.util.List;

@Repository("DAOValidator")
@Transactional
public class DAOValidator {
    private final JdbcTemplate jdbcTemplate;

    public DAOValidator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void validateFilmBd(Long id) {
        String sqlFilmIdList = "SELECT FILM_ID FROM FILMS";
        List<Long> filmIdList = jdbcTemplate.queryForList(sqlFilmIdList, Long.class);
        Validator.validateExistFilm(filmIdList, id);
    }

    public void validateUserBd(Long id) {
        String sqlUserIdList = "SELECT USER_ID FROM USERS";
        List<Long> userIdList = jdbcTemplate.queryForList(sqlUserIdList, Long.class);
        Validator.validateExistUser(userIdList, id);
    }

    public void validateReviewDB(Long id) {
        String sqlReviewIdList = "SELECT REVIEW_ID FROM REVIEW";
        List<Long> reviewIdList = jdbcTemplate.queryForList(sqlReviewIdList, Long.class);
        Validator.validateExistReview(reviewIdList, id);
    }
}
