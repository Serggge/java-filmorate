package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.dao.DAOValidator;
import ru.yandex.practicum.filmorate.storage.dao.EventStorage;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorage;

import java.time.Instant;
import java.util.List;

import static ru.yandex.practicum.filmorate.service.Validator.createValidator;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewStorage reviewStorage;
    private final DAOValidator daoValidator;
    private final EventStorage eventStorage;

    @Override
    public Review create(Review review) {
        createValidator(review);
        daoValidator.validateFilmBd(review.getFilmId());
        daoValidator.validateUserBd(review.getUserId());
        Review savedReview = reviewStorage.update(review);
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .userId(review.getUserId())
                .entityId(review.getReviewId())
                .build();
        eventStorage.save(event);
        log.info("Добавлен ревью: {}", review);
        return savedReview;
    }

    @Override
    public Review update(Review review) {
        daoValidator.validateReviewDB(review.getReviewId());
        daoValidator.validateFilmBd(review.getFilmId());
        daoValidator.validateUserBd(review.getUserId());
        log.debug("Запрос на обновление ревью");
        Review updatedReview = reviewStorage.update(review);
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .eventType(EventType.REVIEW)
                .operation(Operation.UPDATE)
                .userId(review.getUserId())
                .entityId(review.getReviewId())
                .build();
        eventStorage.save(event);
        return updatedReview;
    }

    @Override
    public Review findById(long id) {
        daoValidator.validateReviewDB(id);
        log.debug("Запрос на получение ревью по id = " + id);
        return reviewStorage.findById(id);
    }

    @Override
    public List<Review> findByFilmId(long id, int count) {
        log.debug("Запрос на получение ревью по id фильма: " + id);
        return reviewStorage.findByFilmId(id, count);
    }

    @Override
    public void deleteById(long id) {
        daoValidator.validateReviewDB(id);
        log.debug("Запрос на удаление ревью по id = " + id);
        long userId = reviewStorage.findUserIdByReviewId(id).orElseThrow(
                () -> new ReviewNotFoundException(String.format("Отзыв с id=%d не найден", id))
        );
        reviewStorage.deleteById(id);
        Event event = Event.builder()
                .timestamp(Instant.now().toEpochMilli())
                .eventType(EventType.REVIEW)
                .operation(Operation.REMOVE)
                .userId(userId)
                .entityId(id)
                .build();
        eventStorage.save(event);
    }

    @Override
    public void likeReview(long id, long userId) {
        daoValidator.validateReviewDB(id);
        daoValidator.validateUserBd(userId);
        log.debug("Запрос на лайк ревью по id = " + id + " userID = " + userId);
        reviewStorage.likeReview(id, userId);
    }

    @Override
    public void dislikeReview(long id, long userId) {
        daoValidator.validateReviewDB(id);
        daoValidator.validateUserBd(userId);
        log.debug("Запрос на дизлайк ревью по id = " + id + " userID = " + userId);
        reviewStorage.dislikeReview(id, userId);
    }
}
