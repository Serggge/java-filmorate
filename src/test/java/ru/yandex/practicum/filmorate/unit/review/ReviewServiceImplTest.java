package ru.yandex.practicum.filmorate.unit.review;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.impl.ReviewServiceImpl;
import ru.yandex.practicum.filmorate.storage.dao.DAOValidator;
import ru.yandex.practicum.filmorate.storage.dao.EventStorage;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorage;

@ContextConfiguration(classes = {ReviewServiceImpl.class})
@ExtendWith(SpringExtension.class)
class ReviewServiceImplTest {
    @MockBean(name = "DAOValidator")
    private DAOValidator dAOValidator;

    @MockBean
    private EventStorage eventStorage;

    @Autowired
    private ReviewServiceImpl reviewServiceImpl;

    @MockBean
    private ReviewStorage reviewStorage;

    @Test
    void testCreate() {
        Review review = new Review();
        when(reviewStorage.update((Review) any())).thenReturn(review);
        doNothing().when(dAOValidator).validateFilmBd((Long) any());
        doNothing().when(dAOValidator).validateUserBd((Long) any());
        doNothing().when(eventStorage).save((Event) any());
        Review review1 = mock(Review.class);
        when(review1.getUserId()).thenReturn(1L);
        when(review1.getFilmId()).thenReturn(1L);
        when(review1.getReviewId()).thenReturn(0L);
        assertSame(review, reviewServiceImpl.create(review1));
        verify(reviewStorage).update((Review) any());
        verify(dAOValidator).validateFilmBd((Long) any());
        verify(dAOValidator).validateUserBd((Long) any());
        verify(eventStorage).save((Event) any());
        verify(review1, atLeast(1)).getFilmId();
        verify(review1, atLeast(1)).getReviewId();
        verify(review1, atLeast(1)).getUserId();
    }

    @Test
    void testCreate2() {
        when(reviewStorage.update((Review) any())).thenReturn(new Review());
        doNothing().when(dAOValidator).validateFilmBd((Long) any());
        doNothing().when(dAOValidator).validateUserBd((Long) any());
        doThrow(new ReviewNotFoundException("An error occurred")).when(eventStorage).save((Event) any());
        Review review = mock(Review.class);
        when(review.getUserId()).thenReturn(1L);
        when(review.getFilmId()).thenReturn(1L);
        when(review.getReviewId()).thenReturn(0L);
        assertThrows(ReviewNotFoundException.class, () -> reviewServiceImpl.create(review));
        verify(reviewStorage).update((Review) any());
        verify(dAOValidator).validateFilmBd((Long) any());
        verify(dAOValidator).validateUserBd((Long) any());
        verify(eventStorage).save((Event) any());
        verify(review, atLeast(1)).getFilmId();
        verify(review, atLeast(1)).getReviewId();
        verify(review, atLeast(1)).getUserId();
    }

    @Test
    void testUpdate() {
        Review review = new Review();
        when(reviewStorage.update((Review) any())).thenReturn(review);
        when(reviewStorage.findUserIdByReviewId(anyLong())).thenReturn(Optional.of(42L));
        doNothing().when(dAOValidator).validateFilmBd((Long) any());
        doNothing().when(dAOValidator).validateReviewDB((Long) any());
        doNothing().when(dAOValidator).validateUserBd((Long) any());
        doNothing().when(eventStorage).save((Event) any());
        assertSame(review, reviewServiceImpl.update(new Review()));
        verify(reviewStorage).findUserIdByReviewId(anyLong());
        verify(reviewStorage).update((Review) any());
        verify(dAOValidator).validateFilmBd((Long) any());
        verify(dAOValidator).validateReviewDB((Long) any());
        verify(dAOValidator).validateUserBd((Long) any());
        verify(eventStorage).save((Event) any());
    }

    @Test
    void testUpdate2() {
        when(reviewStorage.update((Review) any())).thenReturn(new Review());
        when(reviewStorage.findUserIdByReviewId(anyLong())).thenReturn(Optional.of(42L));
        doNothing().when(dAOValidator).validateFilmBd((Long) any());
        doNothing().when(dAOValidator).validateReviewDB((Long) any());
        doNothing().when(dAOValidator).validateUserBd((Long) any());
        doThrow(new ReviewNotFoundException("An error occurred")).when(eventStorage).save((Event) any());
        assertThrows(ReviewNotFoundException.class, () -> reviewServiceImpl.update(new Review()));
        verify(reviewStorage).findUserIdByReviewId(anyLong());
        verify(reviewStorage).update((Review) any());
        verify(dAOValidator).validateFilmBd((Long) any());
        verify(dAOValidator).validateReviewDB((Long) any());
        verify(dAOValidator).validateUserBd((Long) any());
        verify(eventStorage).save((Event) any());
    }

    @Test
    void testUpdate3() {
        when(reviewStorage.update((Review) any())).thenReturn(new Review());
        when(reviewStorage.findUserIdByReviewId(anyLong())).thenReturn(Optional.empty());
        doNothing().when(dAOValidator).validateFilmBd((Long) any());
        doNothing().when(dAOValidator).validateReviewDB((Long) any());
        doNothing().when(dAOValidator).validateUserBd((Long) any());
        doNothing().when(eventStorage).save((Event) any());
        assertThrows(ReviewNotFoundException.class, () -> reviewServiceImpl.update(new Review()));
        verify(reviewStorage).findUserIdByReviewId(anyLong());
        verify(dAOValidator).validateFilmBd((Long) any());
        verify(dAOValidator).validateReviewDB((Long) any());
        verify(dAOValidator).validateUserBd((Long) any());
    }

    @Test
    void testFindById() {
        Review review = new Review();
        when(reviewStorage.findById(anyLong())).thenReturn(review);
        doNothing().when(dAOValidator).validateReviewDB((Long) any());
        assertSame(review, reviewServiceImpl.findById(1L));
        verify(reviewStorage).findById(anyLong());
        verify(dAOValidator).validateReviewDB((Long) any());
    }

    @Test
    void testFindByFilmId() {
        ArrayList<Review> reviewList = new ArrayList<>();
        when(reviewStorage.findByFilmId(anyLong(), anyInt())).thenReturn(reviewList);
        List<Review> actualFindByFilmIdResult = reviewServiceImpl.findByFilmId(1L, 3);
        assertSame(reviewList, actualFindByFilmIdResult);
        assertTrue(actualFindByFilmIdResult.isEmpty());
        verify(reviewStorage).findByFilmId(anyLong(), anyInt());
    }

    @Test
    void testFindByFilmId2() {
        when(reviewStorage.findByFilmId(anyLong(), anyInt())).thenThrow(new ReviewNotFoundException("An error occurred"));
        assertThrows(ReviewNotFoundException.class, () -> reviewServiceImpl.findByFilmId(1L, 3));
        verify(reviewStorage).findByFilmId(anyLong(), anyInt());
    }

    @Test
    void testDeleteById() {
        doNothing().when(reviewStorage).deleteById(anyLong());
        when(reviewStorage.findUserIdByReviewId(anyLong())).thenReturn(Optional.of(42L));
        doNothing().when(dAOValidator).validateReviewDB((Long) any());
        doNothing().when(eventStorage).save((Event) any());
        reviewServiceImpl.deleteById(1L);
        verify(reviewStorage).findUserIdByReviewId(anyLong());
        verify(reviewStorage).deleteById(anyLong());
        verify(dAOValidator).validateReviewDB((Long) any());
        verify(eventStorage).save((Event) any());
    }

    @Test
    void testDeleteById2() {
        doNothing().when(reviewStorage).deleteById(anyLong());
        when(reviewStorage.findUserIdByReviewId(anyLong())).thenReturn(Optional.of(42L));
        doNothing().when(dAOValidator).validateReviewDB((Long) any());
        doThrow(new ReviewNotFoundException("An error occurred")).when(eventStorage).save((Event) any());
        assertThrows(ReviewNotFoundException.class, () -> reviewServiceImpl.deleteById(1L));
        verify(reviewStorage).findUserIdByReviewId(anyLong());
        verify(reviewStorage).deleteById(anyLong());
        verify(dAOValidator).validateReviewDB((Long) any());
        verify(eventStorage).save((Event) any());
    }

    @Test
    void testDeleteById3() {
        doNothing().when(reviewStorage).deleteById(anyLong());
        when(reviewStorage.findUserIdByReviewId(anyLong())).thenReturn(Optional.empty());
        doNothing().when(dAOValidator).validateReviewDB((Long) any());
        doNothing().when(eventStorage).save((Event) any());
        assertThrows(ReviewNotFoundException.class, () -> reviewServiceImpl.deleteById(1L));
        verify(reviewStorage).findUserIdByReviewId(anyLong());
        verify(dAOValidator).validateReviewDB((Long) any());
    }

    @Test
    void testLikeReview() {
        doNothing().when(reviewStorage).likeReview((Long) any(), (Long) any());
        doNothing().when(dAOValidator).validateReviewDB((Long) any());
        doNothing().when(dAOValidator).validateUserBd((Long) any());
        reviewServiceImpl.likeReview(1L, 1L);
        verify(reviewStorage).likeReview((Long) any(), (Long) any());
        verify(dAOValidator).validateReviewDB((Long) any());
        verify(dAOValidator).validateUserBd((Long) any());
    }

    @Test
    void testDislikeReview() {
        doNothing().when(reviewStorage).dislikeReview((Long) any(), (Long) any());
        doNothing().when(dAOValidator).validateReviewDB((Long) any());
        doNothing().when(dAOValidator).validateUserBd((Long) any());
        reviewServiceImpl.dislikeReview(1L, 1L);
        verify(reviewStorage).dislikeReview((Long) any(), (Long) any());
        verify(dAOValidator).validateReviewDB((Long) any());
        verify(dAOValidator).validateUserBd((Long) any());
    }

}

