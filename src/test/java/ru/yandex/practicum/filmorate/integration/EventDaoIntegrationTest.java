package ru.yandex.practicum.filmorate.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.impl.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.impl.UserDbStorage;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor__ = @Autowired)
class EventDaoIntegrationTest {

    static Random random = new Random();
    static User user;
    final EventDbStorage eventStorage;
    final UserDbStorage userStorage;

    @BeforeAll
    static void beforeAll() {
        user = User.builder()
                .login("Serggge69")
                .email("serggge69@yandex.ru")
                .name("Sergey")
                .birthday(LocalDate.of(1984, 1, 14))
                .build();
    }

    @BeforeEach
    void beforeEach() {
        user = userStorage.save(user);
    }

    @AfterEach
    void afterEach() {
        eventStorage.deleteAll();
        userStorage.deleteAll();
        user.setId(0);
    }

    @Test
    void testSaveEvent_returnEvent() {
        final Instant timeStamp = Instant.now();
        final long entityId = random.nextInt(32) + 1;
        final Event event = Event.builder()
                .timestamp(timeStamp.toEpochMilli())
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .userId(user.getId())
                .entityId(entityId)
                .build();

        final Event savedEvent = eventStorage.save(event);

        assertThat(savedEvent)
                .isNotNull()
                .isEqualTo(event);
    }

    @Test
    void testFindEvents_returnEventsList() {
        final Instant timeStamp = Instant.now();
        final long entityId = random.nextInt(32) + 1;
        Event event = Event.builder()
                .timestamp(timeStamp.toEpochMilli())
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .userId(user.getId())
                .entityId(entityId)
                .build();
        event = eventStorage.save(event);

        final List<Event> events = eventStorage.findAllByUserId(user.getId());

        assertThat(events)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(event);
    }

}