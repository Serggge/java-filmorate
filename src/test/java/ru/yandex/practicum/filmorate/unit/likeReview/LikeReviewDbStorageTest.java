package ru.yandex.practicum.filmorate.unit.likeReview;

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
import ru.yandex.practicum.filmorate.storage.dao.impl.LikeReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.impl.ReviewDbStorage;

import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {LikeReviewDbStorage.class})
@ExtendWith(SpringExtension.class)
class LikeReviewDbStorageTest {
    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LikeReviewDbStorage likeReviewDbStorage;

    @MockBean(name = "reviewDbStorage")
    private ReviewDbStorage reviewDbStorage;

    @Test
    void testLikeReview() throws DataAccessException {
        when(jdbcTemplate.update(Mockito.<String>any(), (Object[]) any())).thenReturn(1);
        when(reviewDbStorage.findReviewById(anyLong())).thenReturn(new Review());
        doNothing().when(reviewDbStorage).validateReviewDB(Mockito.<Long>any());
        doNothing().when(reviewDbStorage).validateUserBd(Mockito.<Long>any());
        likeReviewDbStorage.likeReview(1L, 1L);
        verify(reviewDbStorage).findReviewById(anyLong());
        verify(reviewDbStorage).validateReviewDB(Mockito.<Long>any());
        verify(reviewDbStorage).validateUserBd(Mockito.<Long>any());
    }

    @Test
    void testDislikeReview() throws DataAccessException {
        when(jdbcTemplate.update(Mockito.<String>any(), (Object[]) any())).thenReturn(1);
        when(reviewDbStorage.findReviewById(anyLong())).thenReturn(new Review());
        doNothing().when(reviewDbStorage).validateReviewDB(Mockito.<Long>any());
        doNothing().when(reviewDbStorage).validateUserBd(Mockito.<Long>any());
        likeReviewDbStorage.dislikeReview(1L, 1L);
        verify(reviewDbStorage).findReviewById(anyLong());
        verify(reviewDbStorage).validateReviewDB(Mockito.<Long>any());
        verify(reviewDbStorage).validateUserBd(Mockito.<Long>any());
    }

}

