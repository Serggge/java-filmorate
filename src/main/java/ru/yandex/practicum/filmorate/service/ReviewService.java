package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {
    Review create(Review review);

    Review update(Review review, Boolean isSave);

    Review findById(long id);

    List<Review> findByFilmId(long id, int count);

    void deleteById(long id);
}
