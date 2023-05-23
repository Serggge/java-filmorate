package ru.yandex.practicum.filmorate.unit.review;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.impl.ReviewServiceImpl;
import ru.yandex.practicum.filmorate.storage.dao.DAOValidator;
import ru.yandex.practicum.filmorate.storage.dao.ReviewStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ReviewServiceImpl.class})
@ExtendWith(SpringExtension.class)
class ReviewServiceImplTest {
    @MockBean(name = "DAOValidator")
    private DAOValidator dAOValidator;

    @Autowired
    private ReviewServiceImpl reviewServiceImpl;

    @MockBean
    private ReviewStorage reviewStorage;

    @Test
    void testCreate() {
        Review review = new Review();
        when(reviewStorage.create(Mockito.<Review>any())).thenReturn(review);
        doNothing().when(dAOValidator).validateFilmBd(Mockito.<Long>any());
        doNothing().when(dAOValidator).validateUserBd(Mockito.<Long>any());
        assertSame(review, reviewServiceImpl.create(new Review()));
        verify(reviewStorage).create(Mockito.<Review>any());
        verify(dAOValidator).validateFilmBd(Mockito.<Long>any());
        verify(dAOValidator).validateUserBd(Mockito.<Long>any());
    }

    @Test
    void testCreate2() {
        when(reviewStorage.create(Mockito.<Review>any())).thenReturn(new Review());
        doNothing().when(dAOValidator).validateFilmBd(Mockito.<Long>any());
        doNothing().when(dAOValidator).validateUserBd(Mockito.<Long>any());
        assertThrows(ValidationException.class, () -> reviewServiceImpl.create(
                new Review(1L, 1L, 1L, "Top content in the world",
                        true, 1, LocalDate.of(1970, 1, 1).atStartOfDay())));
    }

    @Test
    void testUpdate() {
        Review review = new Review();
        when(reviewStorage.update(Mockito.<Review>any())).thenReturn(review);
        doNothing().when(dAOValidator).validateFilmBd(Mockito.<Long>any());
        doNothing().when(dAOValidator).validateReviewDB(Mockito.<Long>any());
        doNothing().when(dAOValidator).validateUserBd(Mockito.<Long>any());
        assertSame(review, reviewServiceImpl.update(new Review(), true));
        verify(reviewStorage).update(Mockito.<Review>any());
        verify(dAOValidator).validateFilmBd(Mockito.<Long>any());
        verify(dAOValidator).validateReviewDB(Mockito.<Long>any());
        verify(dAOValidator).validateUserBd(Mockito.<Long>any());
    }

    @Test
    void testUpdate2() {
        Review review = new Review();
        when(reviewStorage.update(Mockito.<Review>any())).thenReturn(review);
        doNothing().when(dAOValidator).validateFilmBd(Mockito.<Long>any());
        doNothing().when(dAOValidator).validateReviewDB(Mockito.<Long>any());
        doNothing().when(dAOValidator).validateUserBd(Mockito.<Long>any());
        assertSame(review, reviewServiceImpl.update(new Review(), false));
        verify(reviewStorage).update(Mockito.<Review>any());
        verify(dAOValidator).validateFilmBd(Mockito.<Long>any());
        verify(dAOValidator).validateUserBd(Mockito.<Long>any());
    }

    @Test
    void testFindById() {
        Review review = new Review();
        when(reviewStorage.findById(anyLong())).thenReturn(review);
        doNothing().when(dAOValidator).validateReviewDB(Mockito.<Long>any());
        assertSame(review, reviewServiceImpl.findById(1L));
        verify(reviewStorage).findById(anyLong());
        verify(dAOValidator).validateReviewDB(Mockito.<Long>any());
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
        when(reviewStorage.findByFilmId(anyLong(), anyInt())).thenThrow(new ValidationException("An error occurred"));
        assertThrows(ValidationException.class, () -> reviewServiceImpl.findByFilmId(1L, 3));
        verify(reviewStorage).findByFilmId(anyLong(), anyInt());
    }

    @Test
    void testDeleteById() {
        doNothing().when(reviewStorage).deleteById(anyLong());
        doNothing().when(dAOValidator).validateReviewDB(Mockito.<Long>any());
        reviewServiceImpl.deleteById(1L);
        verify(reviewStorage).deleteById(anyLong());
        verify(dAOValidator).validateReviewDB(Mockito.<Long>any());
    }
}

