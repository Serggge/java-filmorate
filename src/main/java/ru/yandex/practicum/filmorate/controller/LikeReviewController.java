package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.storage.LikeReviewStorage;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class LikeReviewController {
    private final LikeReviewStorage likeReviewStorage;

    @PutMapping("/{reviewId}/like/{userId}")
    public void likeReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        likeReviewStorage.likeReview(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public void dislikeReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        likeReviewStorage.dislikeReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void removeLikeReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        likeReviewStorage.dislikeReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void removeDislikeReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        likeReviewStorage.likeReview(reviewId, userId);
    }
}
