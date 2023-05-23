package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review create(Review review);

    Review update(Review review);

    Review findById(long id);

    List<Review> findByFilmId(long filmId, int count);

    void deleteById(long id);

}
