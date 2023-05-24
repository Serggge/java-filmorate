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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataAccessException;
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
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.getInt((String) any())).thenReturn(1);
        when(sqlRowSet.getString((String) any())).thenReturn("String");
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForRowSet((String) any())).thenReturn(sqlRowSet);
        List<Director> actualAll = directorsStorageImpl.getAll();
        assertEquals(2, actualAll.size());
        Director getResult = actualAll.get(0);
        assertEquals("String", getResult.getName());
        Director getResult1 = actualAll.get(1);
        assertEquals("String", getResult1.getName());
        assertEquals(1, getResult1.getId());
        assertEquals(1, getResult.getId());
        verify(jdbcTemplate).queryForRowSet((String) any());
        verify(sqlRowSet, atLeast(1)).next();
        verify(sqlRowSet, atLeast(1)).getInt((String) any());
        verify(sqlRowSet, atLeast(1)).getString((String) any());
    }

    @Test
    void testGetAll2() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.getInt((String) any())).thenThrow(new DataException("An error occurred"));
        when(sqlRowSet.getString((String) any())).thenThrow(new DataException("An error occurred"));
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForRowSet((String) any())).thenReturn(sqlRowSet);
        assertThrows(DataException.class, () -> directorsStorageImpl.getAll());
        verify(jdbcTemplate).queryForRowSet((String) any());
        verify(sqlRowSet).next();
        verify(sqlRowSet).getInt((String) any());
    }

    @Test
    void testGetById() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.getInt((String) any())).thenReturn(1);
        when(sqlRowSet.getString((String) any())).thenReturn("String");
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);
        Director actualById = directorsStorageImpl.getById(1);
        assertEquals(1, actualById.getId());
        assertEquals("String", actualById.getName());
        verify(jdbcTemplate).queryForRowSet((String) any(), (Object[]) any());
        verify(sqlRowSet).next();
        verify(sqlRowSet).getInt((String) any());
        verify(sqlRowSet).getString((String) any());
    }

    @Test
    void testGetById2() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.getInt((String) any())).thenThrow(new DataException("An error occurred"));
        when(sqlRowSet.getString((String) any())).thenThrow(new DataException("An error occurred"));
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);
        assertThrows(DataException.class, () -> directorsStorageImpl.getById(1));
        verify(jdbcTemplate).queryForRowSet((String) any(), (Object[]) any());
        verify(sqlRowSet).next();
        verify(sqlRowSet).getInt((String) any());
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
        when(sqlRowSet.next()).thenThrow(new DirectorNotFoundException("An error occurred"));
        when(jdbcTemplate.update((PreparedStatementCreator) any(), (KeyHolder) any())).thenAnswer((invocation) -> {
            KeyHolder keyHolder = invocation.getArgument(1);
            keyHolder.getKeyList().add(Collections.singletonMap("id", 1));
            return 1;
        });
        when(jdbcTemplate.getDataSource()).thenReturn(mock(DataSource.class));
        doNothing().when(jdbcTemplate).afterPropertiesSet();
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);
        assertThrows(DirectorNotFoundException.class, () -> directorsStorageImpl.create(new Director(1, "Name")));
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
    void testSave3() {
        Film film = mock(Film.class);
        when(film.getDirectors()).thenReturn(new HashSet<>());
        directorsStorageImpl.save(film);
        verify(film).getDirectors();
    }

    @Test
    void testSave4() throws DataAccessException {
        when(jdbcTemplate.batchUpdate((String) any(), (BatchPreparedStatementSetter) any()))
                .thenReturn(new int[]{1, -1, 1, -1});

        HashSet<Director> directorSet = new HashSet<>();
        directorSet
                .add(new Director(1, "INSERT INTO film_directors (film_id, director_id) VALUES (:film_id, :director_id)"));
        Film film = mock(Film.class);
        when(film.getId()).thenReturn(1L);
        when(film.getDirectors()).thenReturn(directorSet);
        directorsStorageImpl.save(film);
        verify(jdbcTemplate).batchUpdate((String) any(), (BatchPreparedStatementSetter) any());
        verify(film).getDirectors();
        verify(film).getId();
    }

    @Test
    void testFindDirectorsByFilmId2() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.getInt((String) any())).thenThrow(new DirectorNotFoundException("An error occurred"));
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForStream((PreparedStatementCreator) any(), (RowMapper<Object>) any())).thenReturn(null);
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);
        assertThrows(DirectorNotFoundException.class, () -> directorsStorageImpl.findDirectorsByFilmId(1L));
        verify(jdbcTemplate).queryForRowSet((String) any(), (Object[]) any());
        verify(sqlRowSet).next();
        verify(sqlRowSet).getInt((String) any());
    }

    @Test
    void testFindAll() {
        assertTrue(directorsStorageImpl.findAll(new ArrayList<>()).isEmpty());
    }

    @Test
    void testFindAll2() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.getInt((String) any())).thenReturn(1);
        when(sqlRowSet.getString((String) any())).thenReturn("String");
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);

        ArrayList<Long> resultLongList = new ArrayList<>();
        resultLongList.add(1L);
        Map<Long, Set<Director>> actualFindAllResult = directorsStorageImpl.findAll(resultLongList);
        assertEquals(1, actualFindAllResult.size());
        assertEquals(1, actualFindAllResult.get(1L).size());
        verify(jdbcTemplate, atLeast(1)).queryForRowSet((String) any(), (Object[]) any());
        verify(sqlRowSet, atLeast(1)).next();
        verify(sqlRowSet, atLeast(1)).getInt((String) any());
        verify(sqlRowSet).getString((String) any());
    }

    @Test
    void testFindAll3() throws DataAccessException {
        SqlRowSet sqlRowSet = mock(SqlRowSet.class);
        when(sqlRowSet.getInt((String) any())).thenThrow(new DataException("An error occurred"));
        when(sqlRowSet.getString((String) any())).thenThrow(new DataException("An error occurred"));
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);

        ArrayList<Long> resultLongList = new ArrayList<>();
        resultLongList.add(1L);
        assertThrows(DataException.class, () -> directorsStorageImpl.findAll(resultLongList));
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
    void testBuildDirector() {
        assertEquals(1, directorsStorageImpl.buildDirector(new Director(1, "Name")).size());
    }

    @Test
    void testBuildDirector3() {
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
        when(sqlRowSet.getInt((String) any())).thenThrow(new DirectorNotFoundException("An error occurred"));
        when(sqlRowSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(jdbcTemplate.queryForRowSet((String) any(), (Object[]) any())).thenReturn(sqlRowSet);
        assertThrows(DirectorNotFoundException.class, () -> directorsStorageImpl.getSortedFilms(1));
        verify(jdbcTemplate).queryForRowSet((String) any(), (Object[]) any());
        verify(sqlRowSet).next();
        verify(sqlRowSet).getInt((String) any());
    }
}

