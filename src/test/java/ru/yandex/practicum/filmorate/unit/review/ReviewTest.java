package ru.yandex.practicum.filmorate.unit.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Review;

class ReviewTest {

    @Test
    void testConstructor() {
        Review actualReview = new Review();
        actualReview.setContent("Not all who wander are lost");
        actualReview.setFilmId(1L);
        actualReview.setIsPositive(true);
        LocalDateTime ofResult = LocalDateTime.of(1, 1, 1, 1, 1);
        actualReview.setReviewDate(ofResult);
        actualReview.setReviewId(1L);
        actualReview.setUseful(1);
        actualReview.setUserId(1L);
        String actualToStringResult = actualReview.toString();
        assertEquals("Not all who wander are lost", actualReview.getContent());
        assertEquals(1L, actualReview.getFilmId());
        assertTrue(actualReview.getIsPositive());
        assertSame(ofResult, actualReview.getReviewDate());
        assertEquals(1L, actualReview.getReviewId());
        assertEquals(1, actualReview.getUseful());
        assertEquals(1L, actualReview.getUserId());
        assertEquals(
                "Review(reviewId=1, filmId=1, userId=1, content=Not all who wander are lost, isPositive=true, useful=1,"
                        + " reviewDate=0001-01-01T01:01)",
                actualToStringResult);
    }

    @Test
    void testConstructor2() {
        Review actualReview = new Review(1L, 1L, 1L, "Not all who wander are lost", true, 1,
                LocalDateTime.of(1, 1, 1, 1, 1));

        assertEquals("Not all who wander are lost", actualReview.getContent());
        assertEquals(1L, actualReview.getUserId());
        assertEquals(1, actualReview.getUseful());
        assertEquals(1L, actualReview.getReviewId());
        assertTrue(actualReview.getIsPositive());
        assertEquals("01:01", actualReview.getReviewDate().toLocalTime().toString());
        assertEquals(1L, actualReview.getFilmId());
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
        Review review1 = new Review();
        assertEquals(review, review1);
        int expectedHashCodeResult = review.hashCode();
        assertEquals(expectedHashCodeResult, review1.hashCode());
    }

    @Test
    void testEquals4() {
        Review review = new Review(1L, 1L, 1L, "Not all who wander are lost", true, 1, LocalDateTime.of(1, 1, 1, 1, 1));
        assertNotEquals(review, new Review());
    }

    @Test
    void testEquals5() {
        Review review = new Review();
        review.setFilmId(1L);
        assertNotEquals(review, new Review());
    }

    @Test
    void testEquals6() {
        Review review = new Review();
        review.setUserId(1L);
        assertNotEquals(review, new Review());
    }

    @Test
    void testEquals7() {
        Review review = new Review();
        review.setContent("Not all who wander are lost");
        assertNotEquals(review, new Review());
    }

    @Test
    void testEquals8() {
        Review review = new Review();
        review.setIsPositive(true);
        assertNotEquals(review, new Review());
    }

    @Test
    void testEquals9() {
        Review review = new Review();
        review.setUseful(1);
        assertNotEquals(review, new Review());
    }

    @Test
    void testEquals10() {
        Review review = new Review();
        review.setReviewDate(LocalDateTime.of(1, 1, 1, 1, 1));
        assertNotEquals(review, new Review());
    }

    @Test
    void testEquals11() {
        Review review = new Review(1L, 1L, 1L, "Not all who wander are lost", true, 1, LocalDateTime.of(1, 1, 1, 1, 1));
        Review review1 = new Review(1L, 1L, 1L, "Not all who wander are lost", true, 1, LocalDateTime.of(1, 1, 1, 1, 1));

        assertEquals(review, review1);
        int expectedHashCodeResult = review.hashCode();
        assertEquals(expectedHashCodeResult, review1.hashCode());
    }

    @Test
    void testEquals12() {
        Review review = new Review();

        Review review1 = new Review();
        review1.setContent("Not all who wander are lost");
        assertNotEquals(review, review1);
    }

    @Test
    void testEquals13() {
        Review review = new Review();

        Review review1 = new Review();
        review1.setIsPositive(true);
        assertNotEquals(review, review1);
    }

    @Test
    void testEquals14() {
        Review review = new Review();

        Review review1 = new Review();
        review1.setReviewDate(LocalDateTime.of(1, 1, 1, 1, 1));
        assertNotEquals(review, review1);
    }
}

