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
        actualReview.setContent("Not all who wander are lost");
        actualReview.setFilmId(1L);
        actualReview.setIsPositive(true);
        LocalDateTime reviewDate = LocalDate.of(1970, 1, 1).atStartOfDay();
        actualReview.setReviewDate(reviewDate);
        actualReview.setReviewId(1L);
        actualReview.setUseful(1);
        actualReview.setUserId(1L);
        String actualToStringResult = actualReview.toString();
        assertEquals("Not all who wander are lost", actualReview.getContent());
        assertEquals(1L, actualReview.getFilmId().longValue());
        assertTrue(actualReview.getIsPositive());
        assertSame(reviewDate, actualReview.getReviewDate());
        assertEquals(1L, actualReview.getReviewId().longValue());
        assertEquals(1, actualReview.getUseful().intValue());
        assertEquals(1L, actualReview.getUserId().longValue());
        assertEquals(
                "Review(reviewId=1, filmId=1, userId=1, content=Not all who wander are lost, isPositive=true, useful=1,"
                        + " reviewDate=1970-01-01T00:00)",
                actualToStringResult);
    }

    @Test
    void testConstructor2() {
        Review actualReview = new Review(1L, 1L, 1L, "Not all who wander are lost", true, 1,
                LocalDate.of(1970, 1, 1).atStartOfDay());
        actualReview.setContent("Not all who wander are lost");
        actualReview.setFilmId(1L);
        actualReview.setIsPositive(true);
        LocalDateTime reviewDate = LocalDate.of(1970, 1, 1).atStartOfDay();
        actualReview.setReviewDate(reviewDate);
        actualReview.setReviewId(1L);
        actualReview.setUseful(1);
        actualReview.setUserId(1L);
        String actualToStringResult = actualReview.toString();
        assertEquals("Not all who wander are lost", actualReview.getContent());
        assertEquals(1L, actualReview.getFilmId().longValue());
        assertTrue(actualReview.getIsPositive());
        assertSame(reviewDate, actualReview.getReviewDate());
        assertEquals(1L, actualReview.getReviewId().longValue());
        assertEquals(1, actualReview.getUseful().intValue());
        assertEquals(1L, actualReview.getUserId().longValue());
        assertEquals(
                "Review(reviewId=1, filmId=1, userId=1, content=Not all who wander are lost, isPositive=true, useful=1,"
                        + " reviewDate=1970-01-01T00:00)",
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
        Review review = new Review(1L, 1L, 1L, "Not all who wander are lost", true, 1,
                LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(review, new Review());
    }

    @Test
    void testEquals5() {
        Review review = new Review();
        assertNotEquals(review,
                new Review(1L, 1L, 1L, "Not all who wander are lost", true, 1, LocalDate.of(1970, 1, 1).atStartOfDay()));
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
        review.setContent("Not all who wander are lost");
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
        review.setReviewDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(review, new Review());
    }

    @Test
    void testEquals11() {
        Review review = new Review(1L, 1L, 1L, "Not all who wander are lost", true, 1,
                LocalDate.of(1970, 1, 1).atStartOfDay());
        Review review2 = new Review(1L, 1L, 1L, "Not all who wander are lost", true, 1,
                LocalDate.of(1970, 1, 1).atStartOfDay());

        assertEquals(review, review2);
        int expectedHashCodeResult = review.hashCode();
        assertEquals(expectedHashCodeResult, review2.hashCode());
    }

    @Test
    void testEquals12() {
        Review review = new Review();

        Review review2 = new Review();
        review2.setFilmId(1L);
        assertNotEquals(review, review2);
    }

    @Test
    void testEquals13() {
        Review review = new Review();

        Review review2 = new Review();
        review2.setUserId(1L);
        assertNotEquals(review, review2);
    }

    @Test
    void testEquals14() {
        Review review = new Review();

        Review review2 = new Review();
        review2.setContent("Not all who wander are lost");
        assertNotEquals(review, review2);
    }

    @Test
    void testEquals15() {
        Review review = new Review();

        Review review2 = new Review();
        review2.setReviewDate(LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(review, review2);
    }

    @Test
    void testEquals16() {
        Review review = new Review();
        review.setUseful(null);
        assertNotEquals(review, new Review());
    }

    @Test
    void testEquals17() {
        Review review = new Review(1L, 1L, 1L, "Not all who wander are lost", false, 1,
                LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(review,
                new Review(1L, 1L, 1L, "Not all who wander are lost", true, 1, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }

    @Test
    void testEquals18() {
        Review review = new Review(1L, 1L, 1L, "Not all who wander are lost", null, 1,
                LocalDate.of(1970, 1, 1).atStartOfDay());
        assertNotEquals(review,
                new Review(1L, 1L, 1L, "Not all who wander are lost", true, 1, LocalDate.of(1970, 1, 1).atStartOfDay()));
    }
}

