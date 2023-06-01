package ru.yandex.practicum.filmorate.integration.review;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.storage.dao.DAOValidator;

@ContextConfiguration(classes = {DAOValidator.class})
@ExtendWith(SpringExtension.class)
class DAOValidatorTest {
    @Autowired
    private DAOValidator dAOValidator;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Test
    void testValidateFilmBd() throws DataAccessException {
        ArrayList<Long> resultLongList = new ArrayList<>();
        resultLongList.add(1L);
        when(jdbcTemplate.queryForList((String) any(), (Class<Long>) any())).thenReturn(resultLongList);
        dAOValidator.validateFilmBd(1L);
        verify(jdbcTemplate).queryForList((String) any(), (Class<Long>) any());
    }

    @Test
    void testValidateUserBd() throws DataAccessException {
        ArrayList<Long> resultLongList = new ArrayList<>();
        resultLongList.add(1L);
        when(jdbcTemplate.queryForList((String) any(), (Class<Long>) any())).thenReturn(resultLongList);
        dAOValidator.validateUserBd(1L);
        verify(jdbcTemplate).queryForList((String) any(), (Class<Long>) any());
    }

    @Test
    void testValidateReviewDB() throws DataAccessException {
        ArrayList<Long> resultLongList = new ArrayList<>();
        resultLongList.add(1L);
        when(jdbcTemplate.queryForList((String) any(), (Class<Long>) any())).thenReturn(resultLongList);
        dAOValidator.validateReviewDB(1L);
        verify(jdbcTemplate).queryForList((String) any(), (Class<Long>) any());
    }

}

