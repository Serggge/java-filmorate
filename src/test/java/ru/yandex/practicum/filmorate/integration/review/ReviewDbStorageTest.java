package ru.yandex.practicum.filmorate.integration.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.DAOValidator;
import ru.yandex.practicum.filmorate.storage.dao.impl.ReviewDbStorage;

@ContextConfiguration(classes = {ReviewDbStorage.class})
@ExtendWith(SpringExtension.class)
class ReviewDbStorageTest {
    @MockBean(name = "DAOValidator")
    private DAOValidator dAOValidator;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReviewDbStorage reviewDbStorage;

    @Test
    void testUpdate() {
        assertThrows(ValidationException.class, () -> reviewDbStorage.update(new Review()));
    }

    @Test
    void testUpdate2() throws DataAccessException {
        when(jdbcTemplate.update((PreparedStatementCreator) any(), (KeyHolder) any())).thenAnswer((invocation) -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(Collections.singletonMap("id", 1));
            return 1;
        });
        Review review = new Review();
        when(jdbcTemplate.queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any())).thenReturn(review);
        assertSame(review, reviewDbStorage
                .update(new Review(1L, 1L, 1L, "Not all who wander are lost", true, 1, LocalDateTime.of(1, 1, 1, 1, 1))));
        verify(jdbcTemplate).update((PreparedStatementCreator) any(), (KeyHolder) any());
        verify(jdbcTemplate).queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any());
    }

    @Test
    void testUpdate3() throws DataAccessException {
        when(jdbcTemplate.update((PreparedStatementCreator) any(), (KeyHolder) any())).thenAnswer((invocation) -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(Collections.singletonMap("id", 1));
            return 1;
        });
        Review review = new Review();
        when(jdbcTemplate.queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any())).thenReturn(review);
        Review review1 = mock(Review.class);
        when(review1.getUseful()).thenReturn(1);
        when(review1.getIsPositive()).thenReturn(true);
        when(review1.getFilmId()).thenReturn(1L);
        when(review1.getReviewId()).thenReturn(1L);
        when(review1.getUserId()).thenReturn(1L);
        doNothing().when(review1).setReviewId(anyLong());
        when(review1.getContent()).thenReturn("Not all who wander are lost");
        assertSame(review, reviewDbStorage.update(review1));
        verify(jdbcTemplate).update((PreparedStatementCreator) any(), (KeyHolder) any());
        verify(jdbcTemplate).queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any());
        verify(review1).getUseful();
        verify(review1, atLeast(1)).getIsPositive();
        verify(review1, atLeast(1)).getContent();
        verify(review1).getFilmId();
        verify(review1, atLeast(1)).getReviewId();
        verify(review1).getUserId();
    }

    @Test
    void testFindById() throws DataAccessException {
        Review review = new Review();
        when(jdbcTemplate.queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any())).thenReturn(review);
        assertSame(review, reviewDbStorage.findById(1L));
        verify(jdbcTemplate).queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any());
    }

    @Test
    void testFindByFilmId() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.getBoolean((String) any())).thenThrow(new ValidationException("An error occurred"));
        when(sqlRowSet.getInt((String) any())).thenThrow(new ValidationException("An error occurred"));
        when(sqlRowSet.getString((String) any())).thenThrow(new ValidationException("An error occurred"));
        when(sqlRowSet.getLong((String) any())).thenThrow(new ValidationException("An error occurred"));
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);
        doThrow(new ValidationException("An error occurred")).when(dAOValidator).validateFilmBd((Long) any());
        assertThrows(ValidationException.class, () -> reviewDbStorage.findByFilmId(0L, 3));
        verify(jdbcTemplate).queryForRowSet((String) any(), (Object[]) any());
        verify(sqlRowSet).next();
        verify(sqlRowSet).getLong((String) any());
    }

    @Test
    void testDeleteById() throws DataAccessException {
        when(jdbcTemplate.update((String) any(), (Object[]) any())).thenReturn(1);
        reviewDbStorage.deleteById(1L);
        verify(jdbcTemplate).update((String) any(), (Object[]) any());
    }

    @Test
    void testLikeReview() throws DataAccessException {
        when(jdbcTemplate.update((String) any(), (Object[]) any())).thenReturn(1);
        when(jdbcTemplate.queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any()))
                .thenReturn(new Review());
        reviewDbStorage.likeReview(1L, 1L);
        verify(jdbcTemplate).queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any());
    }

    @Test
    void testLikeReview2() throws DataAccessException {
        Review review = mock(Review.class);
        when(review.getUseful()).thenReturn(1);
        when(jdbcTemplate.update((String) any(), (Object[]) any())).thenReturn(1);
        when(jdbcTemplate.queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any())).thenReturn(review);
        reviewDbStorage.likeReview(1L, 1L);
        verify(jdbcTemplate).queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any());
        verify(review).getUseful();
    }

    @Test
    void testDislikeReview() throws DataAccessException {
        when(jdbcTemplate.update((String) any(), (Object[]) any())).thenReturn(1);
        when(jdbcTemplate.queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any()))
                .thenReturn(new Review());
        reviewDbStorage.dislikeReview(1L, 1L);
        verify(jdbcTemplate).queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any());
    }

    @Test
    void testDislikeReview2() throws DataAccessException {
        Review review = mock(Review.class);
        when(review.getUseful()).thenReturn(1);
        when(jdbcTemplate.update((String) any(), (Object[]) any())).thenReturn(1);
        when(jdbcTemplate.queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any())).thenReturn(review);
        reviewDbStorage.dislikeReview(1L, 1L);
        verify(jdbcTemplate).queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any());
        verify(review).getUseful();
    }

    @Test
    void testFindUserIdByReviewId() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.getLong((String) any())).thenReturn(1L);
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);
        Optional<Long> actualFindUserIdByReviewIdResult = reviewDbStorage.findUserIdByReviewId(1L);
        assertTrue(actualFindUserIdByReviewIdResult.isPresent());
        assertEquals(1L, actualFindUserIdByReviewIdResult.get());
        verify(jdbcTemplate).queryForRowSet((String) any(), (Object[]) any());
        verify(sqlRowSet).next();
        verify(sqlRowSet).getLong((String) any());
    }

    @Test
    void testFindUserIdByReviewId2() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.getLong((String) any())).thenThrow(new ValidationException("An error occurred"));
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);
        assertThrows(ValidationException.class, () -> reviewDbStorage.findUserIdByReviewId(1L));
        verify(jdbcTemplate).queryForRowSet((String) any(), (Object[]) any());
        verify(sqlRowSet).next();
        verify(sqlRowSet).getLong((String) any());
    }

}

