package ru.yandex.practicum.filmorate.unit.review;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Review;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    void testConstructor() {
        Review actualReview = new Review();
        actualReview.setFilmId(1L);
        LocalDateTime reviewDate = LocalDate.of(1970, 1, 1).atStartOfDay();
        actualReview.setReviewDate(reviewDate);
        actualReview.setReviewId(1L);
        actualReview.setReviewText("Review Text");
        actualReview.setUserId(1L);
        String actualToStringResult = actualReview.toString();
        assertEquals(1L, actualReview.getFilmId().longValue());
        assertSame(reviewDate, actualReview.getReviewDate());
        assertEquals(1L, actualReview.getReviewId().longValue());
        assertEquals("Review Text", actualReview.getReviewText());
        assertEquals(1L, actualReview.getUserId().longValue());
        assertEquals("Review(reviewId=1, filmId=1, userId=1, reviewText=Review Text, reviewDate=1970-01-01T00:00)",
                actualToStringResult);
    }

    @Test
    void testConstructor2() {
        Review actualReview = new Review(1L, 1L, 1L, "Review Text", LocalDate.of(1970, 1, 1).atStartOfDay());
        actualReview.setFilmId(1L);
        LocalDateTime reviewDate = LocalDate.of(1970, 1, 1).atStartOfDay();
        actualReview.setReviewDate(reviewDate);
        actualReview.setReviewId(1L);
        actualReview.setReviewText("Review Text");
        actualReview.setUserId(1L);
        String actualToStringResult = actualReview.toString();
        assertEquals(1L, actualReview.getFilmId().longValue());
        assertSame(reviewDate, actualReview.getReviewDate());
        assertEquals(1L, actualReview.getReviewId().longValue());
        assertEquals("Review Text", actualReview.getReviewText());
        assertEquals(1L, actualReview.getUserId().longValue());
        assertEquals("Review(reviewId=1, filmId=1, userId=1, reviewText=Review Text, reviewDate=1970-01-01T00:00)",
                actualToStringResult);
    }

    @Test
    void testEquals() {
        assertNotEquals(new Review(), null);
        assertNotEquals(new Review(), "Different type to Review");
    }

    @Test
    void testEquals2() {
        Review review = new Review();
        assertEquals(review, review);
        int expectedHashCodeResult = review.hashCode();
        assertEquals(expectedHashCodeResult, review.hashCode());
    }

    @Test
    void testEquals3() {
        Review review = new Review();
        Review review2 = new Review();
        assertEquals(review, review2);
        int expectedHashCodeResult = review.hashCode();
        assertEquals(expectedHashCodeResult, review2.hashCode());
    }

    @Test
    void testEquals4() {
        Review review = new Review(1L, 1L, 1L, "Review Text", LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(review, new Review());
    }

    @Test
    void testEquals5() {
        Review review = new Review();
        assertNotEquals(review, new Review(1L, 1L, 1L, "Review Text", LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    @Test
    void testEquals6() {
        Review review = new Review();
        review.setFilmId(1L);
        assertNotEquals(review, new Review());
    }

    @Test
    void testEquals7() {
        Review review = new Review();
        review.setUserId(1L);
        assertNotEquals(review, new Review());
    }

    @Test
    void testEquals8() {
        Review review = new Review();
        review.setReviewText("Review Text");
        assertNotEquals(review, new Review());
    }

    @Test
    void testEquals9() {
        Review review = new Review();
        review.setReviewDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(review, new Review());
    }

    @Test
    void testEquals10() {
        Review review = new Review(1L, 1L, 1L, "Review Text", LocalDate.of(1970, 1, 1).atStartOfDay());
        Review review2 = new Review(1L, 1L, 1L, "Review Text", LocalDate.of(1970, 1, 1).atStartOfDay());

        assertEquals(review, review2);
        int expectedHashCodeResult = review.hashCode();
        assertEquals(expectedHashCodeResult, review2.hashCode());
    }

    @Test
    void testEquals11() {
        Review review = new Review();

        Review review2 = new Review();
        review2.setFilmId(1L);
        assertNotEquals(review, review2);
    }

    @Test
    void testEquals12() {
        Review review = new Review();

        Review review2 = new Review();
        review2.setUserId(1L);
        assertNotEquals(review, review2);
    }

    @Test
    void testEquals13() {
        Review review = new Review();

        Review review2 = new Review();
        review2.setReviewText("Review Text");
        assertNotEquals(review, review2);
    }

    @Test
    void testEquals14() {
        Review review = new Review();

        Review review2 = new Review();
        review2.setReviewDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(review, review2);
    }
}

