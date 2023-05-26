package ru.yandex.practicum.filmorate.integration.film;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.exception.DataException;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.impl.DirectorsStorageImpl;

@ContextConfiguration(classes = {DirectorsStorageImpl.class})
@ExtendWith(SpringExtension.class)
class DirectorsStorageImplTest {
    @Autowired
    private DirectorsStorageImpl directorsStorageImpl;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Test
    void testGetAll() throws DataAccessException {
        ArrayList<Object> objectList = new ArrayList<>();
        when(jdbcTemplate.query((String) any(), (RowMapper<Object>) any())).thenReturn(objectList);
        List<Director> actualAll = directorsStorageImpl.getAll();
        assertSame(objectList, actualAll);
        assertTrue(actualAll.isEmpty());
        verify(jdbcTemplate).query((String) any(), (RowMapper<Object>) any());
    }

    @Test
    void testGetById() throws DataAccessException {
        Director director = new Director(1, "SELECT id, name FROM directors WHERE id = ?");

        when(jdbcTemplate.queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any()))
                .thenReturn(director);
        assertSame(director, directorsStorageImpl.getById(1));
        verify(jdbcTemplate).queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any());
    }

    @Test
    void testGetById2() throws DataAccessException {
        when(jdbcTemplate.queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any()))
                .thenThrow(new EmptyResultDataAccessException(3));
        assertThrows(DirectorNotFoundException.class, () -> directorsStorageImpl.getById(1));
        verify(jdbcTemplate).queryForObject((String) any(), (RowMapper<Object>) any(), (Object[]) any());
    }

    @Test
    void testCreate() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.update((PreparedStatementCreator) any(), (KeyHolder) any())).thenAnswer((invocation) -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(Collections.singletonMap("id", 1));
            return 1;
        });
        when(jdbcTemplate.getDataSource()).thenReturn(mock(DataSource.class));
        doNothing().when(jdbcTemplate).afterPropertiesSet();
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);
        assertThrows(DataException.class, () -> directorsStorageImpl.create(new Director(1, "Name")));
        verify(jdbcTemplate).queryForRowSet((String) any(), (Object[]) any());
        verify(sqlRowSet).next();
    }

    @Test
    void testCreate2() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.next()).thenThrow(new EmptyResultDataAccessException(3));
        when(jdbcTemplate.update((PreparedStatementCreator) any(), (KeyHolder) any())).thenAnswer((invocation) -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(Collections.singletonMap("id", 1));
            return 1;
        });
        when(jdbcTemplate.getDataSource()).thenReturn(mock(DataSource.class));
        doNothing().when(jdbcTemplate).afterPropertiesSet();
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);
        assertThrows(EmptyResultDataAccessException.class, () -> directorsStorageImpl.create(new Director(1, "Name")));
        verify(jdbcTemplate).queryForRowSet((String) any(), (Object[]) any());
        verify(sqlRowSet).next();
    }

    @Test
    void testUpdate() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.update((String) any(), (Object[]) any())).thenReturn(1);
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);
        Director director = new Director(1, "Name");

        assertSame(director, directorsStorageImpl.update(director));
        verify(jdbcTemplate).queryForRowSet((String) any(), (Object[]) any());
        verify(sqlRowSet).next();
    }

    @Test
    void testDelete() throws DataAccessException {
        when(jdbcTemplate.update((String) any(), (Object[]) any())).thenReturn(1);
        directorsStorageImpl.delete(1);
        verify(jdbcTemplate).update((String) any(), (Object[]) any());
    }

    @Test
    void testSave() {
        Film film = new Film();
        assertSame(film, directorsStorageImpl.save(film));
    }

    @Test
    void testSave2() throws DataAccessException {
        when(jdbcTemplate.batchUpdate((String) any(), (BatchPreparedStatementSetter) any()))
                .thenReturn(new int[]{1, -1, 1, -1});

        Film film = new Film();
        film.addDirector(
                new Director(1, "INSERT INTO film_directors (film_id, director_id) VALUES (:film_id, :director_id)"));
        assertSame(film, directorsStorageImpl.save(film));
        verify(jdbcTemplate).batchUpdate((String) any(), (BatchPreparedStatementSetter) any());
    }

    @Test
    void testFindDirectorsByFilmId() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.getInt((String) any())).thenThrow(new EmptyResultDataAccessException(3));
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForStream((PreparedStatementCreator) any(), (RowMapper<Object>) any())).thenReturn(null);
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);
        assertThrows(EmptyResultDataAccessException.class, () -> directorsStorageImpl.findDirectorsByFilmId(1L));
        verify(jdbcTemplate).queryForRowSet((String) any(), (Object[]) any());
        verify(sqlRowSet).next();
        verify(sqlRowSet).getInt((String) any());
    }

    @Test
    void testDeleteByFilmId() throws DataAccessException {
        when(jdbcTemplate.update((String) any(), (Object[]) any())).thenReturn(1);
        directorsStorageImpl.deleteByFilmId(1L);
        verify(jdbcTemplate).update((String) any(), (Object[]) any());
    }

    @Test
    void testFindBySubString() throws DataAccessException {
        ArrayList<Long> resultLongList = new ArrayList<>();
        when(jdbcTemplate.queryForList((String) any(), (Class<Long>) any(), (Object[]) any())).thenReturn(resultLongList);
        List<Long> actualFindBySubStringResult = directorsStorageImpl.findBySubString("Substring");
        assertSame(resultLongList, actualFindBySubStringResult);
        assertTrue(actualFindBySubStringResult.isEmpty());
        verify(jdbcTemplate).queryForList((String) any(), (Class<Long>) any(), (Object[]) any());
    }

    @Test
    void testExistsById() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);
        assertTrue(directorsStorageImpl.existsById(1));
        verify(jdbcTemplate).queryForRowSet((String) any(), (Object[]) any());
        verify(sqlRowSet).next();
    }

    @Test
    void testExistsById2() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.next()).thenThrow(new EmptyResultDataAccessException(3));
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);
        assertThrows(EmptyResultDataAccessException.class, () -> directorsStorageImpl.existsById(1));
        verify(jdbcTemplate).queryForRowSet((String) any(), (Object[]) any());
        verify(sqlRowSet).next();
    }

    @Test
    void testBuildDirector() {
        assertEquals(1, directorsStorageImpl.buildDirector(new Director(1, "Name")).size());
    }

    @Test
    void testBuildDirector2() {
        Director director = mock(Director.class);
        when(director.getName()).thenReturn("Name");
        assertEquals(1, directorsStorageImpl.buildDirector(director).size());
        verify(director).getName();
    }

    @Test
    void testGetSortedFilms() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.getInt((String) any())).thenReturn(1);
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);
        List<Long> actualSortedFilms = directorsStorageImpl.getSortedFilms(1);
        assertEquals(2, actualSortedFilms.size());
        assertEquals(1L, actualSortedFilms.get(0));
        assertEquals(1L, actualSortedFilms.get(1));
        verify(jdbcTemplate).queryForRowSet((String) any(), (Object[]) any());
        verify(sqlRowSet, atLeast(1)).next();
        verify(sqlRowSet, atLeast(1)).getInt((String) any());
    }

    @Test
    void testGetSortedFilms2() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.getInt((String) any())).thenThrow(new EmptyResultDataAccessException(3));
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);
        assertThrows(EmptyResultDataAccessException.class, () -> directorsStorageImpl.getSortedFilms(1));
        verify(jdbcTemplate).queryForRowSet((String) any(), (Object[]) any());
        verify(sqlRowSet).next();
        verify(sqlRowSet).getInt((String) any());
    }

    @Test
    void testDeleteAll() throws DataAccessException {
        when(jdbcTemplate.update((String) any())).thenReturn(1);
        directorsStorageImpl.deleteAll();
        verify(jdbcTemplate).update((String) any());
    }

}

