package ru.yandex.practicum.filmorate.integration.review;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        when(jdbcTemplate.update(Mockito.<PreparedStatementCreator>any(), Mockito.<KeyHolder>any()))
                .thenAnswer((invocation) -> {
                    KeyHolder keyHolder = invocation.getArgument(1);
                    keyHolder.getKeyList().add(Collections.singletonMap("id", 1));
                    return 1;
                });
        Review review = new Review();
        when(jdbcTemplate.queryForObject(Mockito.<String>any(), Mockito.<RowMapper<Object>>any(), (Object[]) any()))
                .thenReturn(review);
        assertSame(review, reviewDbStorage.update(
                new Review(1L, 1L, 1L, "Not all who wander are lost", true, 1,
                        LocalDate.of(1970, 1, 1).atStartOfDay())));
        verify(jdbcTemplate).update(Mockito.<PreparedStatementCreator>any(), Mockito.<KeyHolder>any());
        verify(jdbcTemplate).queryForObject(Mockito.<String>any(), Mockito.<RowMapper<Object>>any(), (Object[]) any());
    }

    @Test
    void testUpdate3() throws DataAccessException {
        when(jdbcTemplate.update(Mockito.<PreparedStatementCreator>any(), Mockito.<KeyHolder>any()))
                .thenAnswer((invocation) -> {
                    KeyHolder keyHolder = invocation.getArgument(1);
                    keyHolder.getKeyList().add(Collections.singletonMap("id", 1));
                    return 1;
                });
        Review review = new Review();
        when(jdbcTemplate.queryForObject(Mockito.<String>any(), Mockito.<RowMapper<Object>>any(), (Object[]) any()))
                .thenReturn(review);
        Review review2 = mock(Review.class);
        when(review2.getUseful()).thenReturn(1);
        when(review2.getIsPositive()).thenReturn(true);
        when(review2.getFilmId()).thenReturn(1L);
        when(review2.getReviewId()).thenReturn(1L);
        when(review2.getUserId()).thenReturn(1L);
        doNothing().when(review2).setReviewId(anyLong());
        when(review2.getContent()).thenReturn("Not all who wander are lost");
        assertSame(review, reviewDbStorage.update(review2));
        verify(jdbcTemplate).update(Mockito.<PreparedStatementCreator>any(), Mockito.<KeyHolder>any());
        verify(jdbcTemplate).queryForObject(Mockito.<String>any(), Mockito.<RowMapper<Object>>any(), (Object[]) any());
        verify(review2).getUseful();
        verify(review2, atLeast(1)).getIsPositive();
        verify(review2, atLeast(1)).getContent();
        verify(review2).getFilmId();
        verify(review2, atLeast(1)).getReviewId();
        verify(review2).getUserId();
    }

    @Test
    void testFindById() throws DataAccessException {
        Review review = new Review();
        when(jdbcTemplate.queryForObject(Mockito.<String>any(), Mockito.<RowMapper<Object>>any(), (Object[]) any()))
                .thenReturn(review);
        assertSame(review, reviewDbStorage.findById(1L));
        verify(jdbcTemplate).queryForObject(Mockito.<String>any(), Mockito.<RowMapper<Object>>any(), (Object[]) any());
    }

    @Test
    void testFindByFilmId() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.getBoolean(Mockito.<String>any())).thenThrow(new ValidationException("An error occurred"));
        when(sqlRowSet.getInt(Mockito.<String>any())).thenThrow(new ValidationException("An error occurred"));
        when(sqlRowSet.getString(Mockito.<String>any())).thenThrow(new ValidationException("An error occurred"));
        when(sqlRowSet.getLong(Mockito.<String>any())).thenThrow(new ValidationException("An error occurred"));
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForRowSet(Mockito.<String>any(), (Object[]) any())).thenReturn(sqlRowSet);
        doThrow(new ValidationException("An error occurred")).when(dAOValidator).validateFilmBd(Mockito.<Long>any());
        assertThrows(ValidationException.class, () -> reviewDbStorage.findByFilmId(0L, 3));
        verify(jdbcTemplate).queryForRowSet(Mockito.<String>any(), (Object[]) any());
        verify(sqlRowSet).next();
        verify(sqlRowSet).getLong(Mockito.<String>any());
    }

    @Test
    void testFindByFilmId2() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.getBoolean(Mockito.<String>any())).thenReturn(true);
        when(sqlRowSet.getInt(Mockito.<String>any())).thenReturn(1);
        when(sqlRowSet.getString(Mockito.<String>any())).thenReturn("String");
        when(sqlRowSet.getLong(Mockito.<String>any())).thenReturn(1L);
        when(sqlRowSet.next()).thenReturn(false).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForRowSet(Mockito.<String>any(), (Object[]) any())).thenReturn(sqlRowSet);
        doThrow(new ValidationException("An error occurred")).when(dAOValidator).validateFilmBd(Mockito.<Long>any());
        assertTrue(reviewDbStorage.findByFilmId(0L, 3).isEmpty());
        verify(jdbcTemplate).queryForRowSet(Mockito.<String>any(), (Object[]) any());
        verify(sqlRowSet).next();
    }

    @Test
    void testDeleteById() throws DataAccessException {
        when(jdbcTemplate.update(Mockito.<String>any(), (Object[]) any())).thenReturn(1);
        reviewDbStorage.deleteById(1L);
        verify(jdbcTemplate).update(Mockito.<String>any(), (Object[]) any());
    }

    @Test
    void testLikeReview() throws DataAccessException {
        when(jdbcTemplate.update(Mockito.<String>any(), (Object[]) any())).thenReturn(1);
        when(jdbcTemplate.queryForObject(Mockito.<String>any(), Mockito.<RowMapper<Object>>any(), (Object[]) any()))
                .thenReturn(new Review());
        reviewDbStorage.likeReview(1L, 1L);
        verify(jdbcTemplate).queryForObject(Mockito.<String>any(), Mockito.<RowMapper<Object>>any(), (Object[]) any());
    }

    @Test
    void testLikeReview2() throws DataAccessException {
        Review review = mock(Review.class);
        when(review.getUseful()).thenReturn(1);
        when(jdbcTemplate.update(Mockito.<String>any(), (Object[]) any())).thenReturn(1);
        when(jdbcTemplate.queryForObject(Mockito.<String>any(), Mockito.<RowMapper<Object>>any(), (Object[]) any()))
                .thenReturn(review);
        reviewDbStorage.likeReview(1L, 1L);
        verify(jdbcTemplate).queryForObject(Mockito.<String>any(), Mockito.<RowMapper<Object>>any(), (Object[]) any());
        verify(review).getUseful();
    }

    @Test
    void testDislikeReview() throws DataAccessException {
        when(jdbcTemplate.update(Mockito.<String>any(), (Object[]) any())).thenReturn(1);
        when(jdbcTemplate.queryForObject(Mockito.<String>any(), Mockito.<RowMapper<Object>>any(), (Object[]) any()))
                .thenReturn(new Review());
        reviewDbStorage.dislikeReview(1L, 1L);
        verify(jdbcTemplate).queryForObject(Mockito.<String>any(), Mockito.<RowMapper<Object>>any(), (Object[]) any());
    }

    @Test
    void testDislikeReview2() throws DataAccessException {
        Review review = mock(Review.class);
        when(review.getUseful()).thenReturn(1);
        when(jdbcTemplate.update(Mockito.<String>any(), (Object[]) any())).thenReturn(1);
        when(jdbcTemplate.queryForObject(Mockito.<String>any(), Mockito.<RowMapper<Object>>any(), (Object[]) any()))
                .thenReturn(review);
        reviewDbStorage.dislikeReview(1L, 1L);
        verify(jdbcTemplate).queryForObject(Mockito.<String>any(), Mockito.<RowMapper<Object>>any(), (Object[]) any());
        verify(review).getUseful();
    }
}

