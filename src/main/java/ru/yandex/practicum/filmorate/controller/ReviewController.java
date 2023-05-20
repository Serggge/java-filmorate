package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorage;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor(onConstructor__ = @Autowired)
public class ReviewController {
    private final ReviewStorage reviewStorage;

    @PostMapping()
    public Review postReview(@RequestBody Review review) {
        return reviewStorage.create(review);
    }

    @PutMapping()
    public Review putReview(@RequestBody Review review) {
        return reviewStorage.save(review, true);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable int id) {
        return reviewStorage.findReviewById(id);
    }

    @GetMapping("/byFilm/{id}")
    public List<Review> getReviewsByFilmId(@PathVariable int id) {
        return reviewStorage.findReviewsByFilmId(id);
    }

    @DeleteMapping("/{id}")
    public void deleteFriends(@PathVariable int id) {
        reviewStorage.deleteReviewById(id);
    }
}
