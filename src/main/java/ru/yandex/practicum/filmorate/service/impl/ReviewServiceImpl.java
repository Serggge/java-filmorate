package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.dao.DAOValidator;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage storage;
    private final DAOValidator daoValidator;

    @Override
    public Review create(Review review) {
        if (review.getReviewId() != null) {
            throw new ValidationException("Недопустимый параметр ID при создании ревью");
        }
        daoValidator.validateFilmBd(review.getFilmId());
        daoValidator.validateUserBd(review.getUserId());
        log.info("Добавлен ревью: {}", review);
        return storage.create(review);
    }

    @Override
    public Review update(Review review, Boolean isUpdate) {
        if (isUpdate) {
            daoValidator.validateReviewDB(review.getReviewId());
        }
        daoValidator.validateFilmBd(review.getFilmId());
        daoValidator.validateUserBd(review.getUserId());
        log.debug("Запрос на обновление ревью");
        return storage.update(review);
    }

    @Override
    public Review findById(long id) {
        daoValidator.validateReviewDB(id);
        log.debug("Запрос на получение ревью по id = " + id);
        return storage.findById(id);
    }

    @Override
    public List<Review> findByFilmId(long id, int count) {
        log.debug("Запрос на получение ревью по id фильма: " + id);
        return storage.findByFilmId(id, count);
    }

    @Override
    public void deleteById(long id) {
        daoValidator.validateReviewDB(id);
        log.debug("Запрос на удаление ревью по id = " + id);
        storage.deleteById(id);
    }
}
