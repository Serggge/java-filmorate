package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.LikeReviewStorage;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class ReviewController {
    private final ReviewService service;
    private final LikeReviewStorage likeReviewStorage;

    @PostMapping()
    public Review postReview(@RequestBody Review review) {
        return service.create(review);
    }

    @PutMapping()
    public Review putReview(@RequestBody Review review) {
        return service.update(review, true);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable int id) {
        return service.findById(id);
    }

    @GetMapping()
    public List<Review> getReviewsByFilmId(@RequestParam(defaultValue = "0") int filmId, @RequestParam(defaultValue = "10") int count) {
        return service.findByFilmId(filmId, count);
    }

    @DeleteMapping("/{id}")
    public void deleteFriends(@PathVariable int id) {
        service.deleteById(id);
    }

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
