package ru.yandex.practicum.filmorate.integration.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.dao.MpaStorage;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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
        final Collection<Mpa> mpas = mpaStorage.findAll();

        assertThat(mpas)
                .isNotNull()
                .isNotEmpty()
                .hasSize(MpaRating.values().length);
        assertThat(mpas.containsAll(List.of(MpaRating.values())));
    }

    @Test
    void existsById() {
        final int id = new Random().nextInt(MpaRating.values().length - 1) + 1;

        assertTrue(mpaStorage.existsById(id));
    }

}