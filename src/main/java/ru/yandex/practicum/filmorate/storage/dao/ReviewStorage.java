package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review create(Review review);

    Review save(Review review, Boolean isSave);

    Review findReviewById(long id);

    List<Review> findReviewsByFilmId(long filmId, int count);

    void deleteReviewById(long id);

}
