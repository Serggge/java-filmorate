package ru.yandex.practicum.filmorate.unit.review;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.impl.ReviewDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ReviewDbStorage.class})
@ExtendWith(SpringExtension.class)
class ReviewDbStorageTest {
    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReviewDbStorage reviewDbStorage;

    @Test
    void testCreate() throws DataAccessException {
        when(jdbcTemplate.query(Mockito.<String>any(), Mockito.<RowMapper<Object>>any())).thenReturn(new ArrayList<>());
        Review review = new Review(1L, 1L, 1L, "ReviewOne", LocalDate.of(1970, 1, 1).atStartOfDay());
        assertThrows(ValidationException.class, () -> reviewDbStorage
                .create(review));
    }
}

