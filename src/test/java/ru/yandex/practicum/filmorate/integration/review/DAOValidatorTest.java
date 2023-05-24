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
import ru.yandex.practicum.filmorate.storage.dao.DAOValidator;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(jdbcTemplate.queryForList(Mockito.<String>any(), Mockito.<Class<Long>>any())).thenReturn(resultLongList);
        dAOValidator.validateFilmBd(1L);
        verify(jdbcTemplate).queryForList(Mockito.<String>any(), Mockito.<Class<Long>>any());
    }

    @Test
    void testValidateReviewDB() throws DataAccessException {
        ArrayList<Long> resultLongList = new ArrayList<>();
        resultLongList.add(1L);
        when(jdbcTemplate.queryForList(Mockito.<String>any(), Mockito.<Class<Long>>any())).thenReturn(resultLongList);
        dAOValidator.validateReviewDB(1L);
        verify(jdbcTemplate).queryForList(Mockito.<String>any(), Mockito.<Class<Long>>any());
    }
}

