package ru.yandex.practicum.filmorate.integration.review;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.dao.DAOValidator;
import ru.yandex.practicum.filmorate.storage.dao.impl.LikeReviewDbStorage;

import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {LikeReviewDbStorage.class})
@ExtendWith(SpringExtension.class)
class LikeReviewDbStorageTest {
    @MockBean(name = "DAOValidator")
    private DAOValidator dAOValidator;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LikeReviewDbStorage likeReviewDbStorage;

    @MockBean
    private ReviewService reviewService;

    @Test
    void testLikeReview() throws DataAccessException {
        when(jdbcTemplate.update(Mockito.<String>any(), (Object[]) any())).thenReturn(1);
        when(reviewService.findById(anyLong())).thenReturn(new Review());
        doNothing().when(dAOValidator).validateReviewDB(Mockito.<Long>any());
        doNothing().when(dAOValidator).validateUserBd(Mockito.<Long>any());
        likeReviewDbStorage.likeReview(1L, 1L);
        verify(reviewService).findById(anyLong());
        verify(dAOValidator).validateReviewDB(Mockito.<Long>any());
        verify(dAOValidator).validateUserBd(Mockito.<Long>any());
    }

    @Test
    void testDislikeReview() throws DataAccessException {
        when(jdbcTemplate.update(Mockito.<String>any(), (Object[]) any())).thenReturn(1);
        when(reviewService.findById(anyLong())).thenReturn(new Review());
        doNothing().when(dAOValidator).validateReviewDB(Mockito.<Long>any());
        doNothing().when(dAOValidator).validateUserBd(Mockito.<Long>any());
        likeReviewDbStorage.dislikeReview(1L, 1L);
        verify(reviewService).findById(anyLong());
        verify(dAOValidator).validateReviewDB(Mockito.<Long>any());
        verify(dAOValidator).validateUserBd(Mockito.<Long>any());
    }
}

