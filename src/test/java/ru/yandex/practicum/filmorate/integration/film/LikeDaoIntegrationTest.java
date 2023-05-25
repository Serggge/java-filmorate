package ru.yandex.practicum.filmorate.integration.film;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.LikeStorage;
import ru.yandex.practicum.filmorate.storage.dao.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.impl.UserDbStorage;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
class LikeDaoIntegrationTest {

    static FilmDbStorage filmStorage;
    static UserDbStorage userStorage;
    static final Film film = new Film();
    static final User user = new User();
    static final User friend = new User();
    final LikeStorage likeStorage;

    @Autowired
    public LikeDaoIntegrationTest(LikeStorage likeStorage, FilmDbStorage filmDbStorage, UserDbStorage userDbStorage) {
        this.likeStorage = likeStorage;
        filmStorage = filmDbStorage;
        userStorage = userDbStorage;
        setFilmAndUserForDefaults();
        filmStorage.save(film);
        userStorage.save(user);
        userStorage.save(friend);
    }

    @AfterEach
    void afterEach() {
        likeStorage.deleteAll();
        filmStorage.deleteAll();
        userStorage.deleteAll();
    }

    @Test
    void testSaveAndFind() {
        final Like like = new Like(film.getId(), user.getId());

        likeStorage.save(like);
        final List<Long> usersId = likeStorage.findUsersIdByFilmId(film.getId());

        assertThat(usersId)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(user.getId());
    }

    @Test
    void deleteById() {
        final Like like = new Like(film.getId(), user.getId());

        likeStorage.save(like);
        likeStorage.delete(like);
        final List<Long> usersId = likeStorage.findUsersIdByFilmId(film.getId());

        assertThat(usersId)
                .isNotNull()
                .isEmpty();
    }

    @Test
    void testIsExist() {
        final Like like = new Like(film.getId(), user.getId());

        likeStorage.save(like);
        assertThat(likeStorage.isExist(like)).isTrue();

        likeStorage.delete(like);
        assertThat(likeStorage.isExist(like)).isFalse();
    }

    @Test
    void testFindAll() {
        final Like like = new Like(film.getId(), user.getId());

        likeStorage.save(like);
        Map<Long, Set<Long>> all = likeStorage.findAll(Collections.singleton(film.getId()));

        assertThat(all.get(film.getId())).isNotEmpty();
        assertThat(all.get(film.getId())).hasSize(1);
        assertThat(all.get(film.getId())).contains(user.getId());
    }

    @Test
    void testfindCommonLikes() {
        final Like like = new Like(film.getId(), user.getId());
        final Like likeFriend = new Like(film.getId(), friend.getId());

        likeStorage.save(like);
        likeStorage.save(likeFriend);
        List<Long> commonLikes = likeStorage.findCommonLikes(user.getId(), friend.getId());

        assertThat(commonLikes.contains(film.getId())).isTrue();
        assertThat(commonLikes.size() == 1).isTrue();
    }

    @Test
    void testSuggestFilms() {
        Film secondFilm = Film.builder()
                .name("Second film")
                .description("Description second film")
                .releaseDate(LocalDate.now())
                .duration(200)
                .mpa(new Mpa(2))
                .build();
        User secondUser = User.builder()
                .login("Peter555")
                .name("Peter")
                .email("peter@ya.ru")
                .birthday(LocalDate.of(2010, 1, 1))
                .build();
        secondFilm = filmStorage.save(secondFilm);
        secondUser = userStorage.save(secondUser);
        final Like firstUserFirstFilm = new Like(film.getId(), user.getId());
        likeStorage.save(firstUserFirstFilm);
        final Like secondUserFirstFilm = new Like(film.getId(), secondUser.getId());
        likeStorage.save(secondUserFirstFilm);
        final Like secondUserSecondFilm = new Like(secondFilm.getId(), secondUser.getId());
        likeStorage.save(secondUserSecondFilm);

        List<Long> suggestForUser = likeStorage.suggestFilms(user.getId());

        assertThat(suggestForUser)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1)
                .contains(secondFilm.getId());
    }

    private static void setFilmAndUserForDefaults() {
        film.setId(0);
        film.setName("Film One");
        film.setDescription("Description for first film");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpa(new Mpa(1));

        user.setId(0);
        user.setLogin("Ivan2000");
        user.setEmail("ivan2000@yandex.ru");
        user.setName("Ivan");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        friend.setId(0);
        friend.setLogin("Petr2000");
        friend.setEmail("petr2000@yandex.ru");
        friend.setName("Petr");
        friend.setBirthday(LocalDate.of(2000, 1, 1));
    }

}