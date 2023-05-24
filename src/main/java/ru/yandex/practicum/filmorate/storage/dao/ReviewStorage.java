package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review update(Review review);

    Review findById(long id);

    List<Review> findByFilmId(long filmId, int count);

    void deleteById(long id);

    void likeReview(Long id, Long userId);

    void dislikeReview(Long id, Long userId);
}
