package ru.yandex.practicum.filmorate.integration.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;
import java.util.*;
import java.util.stream.Collectors;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor__ = @Autowired)
class MpaDaoIntegrationTest {

    final MpaStorage mpaStorage;

    @Test
    void findById() {
        final int id = 3;
        final Optional<Mpa> mpaOptional = mpaStorage.findById(id);

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa).
                                hasFieldOrPropertyWithValue("name", MpaRating.values()[id - 1].getName())
                                .hasFieldOrPropertyWithValue("name", "PG-13"));
    }

    @Test
    void findAll() {
        List<Mpa> allMpaRatings = Arrays.stream(MpaRating.values())
                .map(mpaRating -> new Mpa(mpaRating.ordinal() + 1))
                .collect(Collectors.toList());

        final Collection<Mpa> mpas = mpaStorage.findAll();

        assertThat(mpas)
                .isNotNull()
                .isNotEmpty()
                .hasSize(MpaRating.values().length)
                .containsAll(allMpaRatings)
                .isEqualTo(allMpaRatings);
    }

    @Test
    void existsById() {
        final int id = new Random().nextInt(MpaRating.values().length - 1) + 1;

        assertTrue(mpaStorage.existsById(id));
    }

}