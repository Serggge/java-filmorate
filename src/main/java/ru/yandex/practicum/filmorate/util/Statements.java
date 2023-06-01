package ru.yandex.practicum.filmorate.util;

public class Statements {

    private Statements() {
    }

    public static final String STATEMENT_FOR_FILM =
            "SELECT films.film_id, films.name as film_name, description, release_date, duration, mpa_id, " +
            "genres.genre_id as genre_id, directors.id as director_id, directors.name as director_name, " +
            "likes.user_id FROM films " +
            "LEFT JOIN film_genre ON film_genre.film_id = films.film_id " +
            "LEFT JOIN genres ON film_genre.genre_id = genres.genre_id " +
            "LEFT JOIN film_directors ON film_directors.film_id = films.film_id " +
            "LEFT JOIN directors ON directors.id = film_directors.director_id " +
            "LEFT JOIN likes ON likes.film_id = films.film_id";

}
