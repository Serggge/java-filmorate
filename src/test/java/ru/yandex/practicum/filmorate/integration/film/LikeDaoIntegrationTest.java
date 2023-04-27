package ru.yandex.practicum.filmorate.integration.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor__ = @Autowired)
class LikeDaoIntegrationTest {

    final LikeStorage likeStorage;

    @BeforeEach
    void beforeEach() {

    }

    @AfterEach
    void afterEach() {

    }

    @Test
    void save() {
    }

    @Test
    void findAllById() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void getPopular() {
    }

    @Test
    void isExist() {
    }
}