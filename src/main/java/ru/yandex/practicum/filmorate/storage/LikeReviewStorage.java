package ru.yandex.practicum.filmorate.storage;

public interface LikeReviewStorage {
    void likeReview(Long id, Long userId);

    void dislikeReview(Long id, Long userId);
}
