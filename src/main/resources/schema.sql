DROP TABLE IF EXISTS mpa CASCADE;
CREATE TABLE mpa
(
    mpa_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name   varchar(30) NOT NULL UNIQUE
);

DROP TABLE IF EXISTS films CASCADE;
CREATE TABLE films
(
    film_id      bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         varchar(255)  NOT NULL,
    description  varchar(1000) NOT NULL,
    release_date date          NOT NULL,
    duration     int           NOT NULL,
    mpa_id       int REFERENCES mpa (mpa_id) ON DELETE CASCADE,
    CONSTRAINT release_after_first_film CHECK (release_date > '1895-12-28'),
    CONSTRAINT film_not_blank_fields CHECK (name <> '' AND description <> '' AND duration > 0)
);

DROP TABLE IF EXISTS genres CASCADE;
CREATE TABLE genres
(
    genre_id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     varchar(255) NOT NULL UNIQUE
);

drop table IF EXISTs directors CASCADE;
create TABLE  directors (
	id int GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	name varchar(255) NOT NULL
);

drop table IF EXISTs film_directors CASCADE;
create TABLE  film_directors (
	film_id int REFERENCES films (film_id) ON delete CASCADE,
	director_id int REFERENCES directors (id) ON delete CASCADE,
CONSTRAINT pk_film_directors PRIMARY KEY (film_id, director_id)
);

create TABLE IF NOT EXISTS film_genre (
	film_id int REFERENCES films (film_id) ON delete CASCADE,
	genre_id int REFERENCES genres (genre_id) ON delete CASCADE,
CONSTRAINT pk_fields_film_genre PRIMARY KEY (film_id, genre_id)
);

DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users
(
    user_id  bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    login    varchar(100) NOT NULL UNIQUE,
    email    varchar(100) NOT NULL UNIQUE,
    name     varchar(100),
    birthday date         NOT NULL,
    CONSTRAINT users_not_blank_fields CHECK (login <> '' AND email <> ''),
    CONSTRAINT birthday_past CHECK (birthday < NOW())
);

DROP TABLE IF EXISTS friends CASCADE;
CREATE TABLE friends
(
    user_id   bigint NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    friend_id bigint NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    confirmed boolean DEFAULT false,
    CONSTRAINT pk_fields_friends PRIMARY KEY (user_id, friend_id)
);

DROP TABLE IF EXISTS likes CASCADE;
CREATE TABLE likes
(
    film_id bigint REFERENCES films (film_id) ON DELETE CASCADE,
    user_id bigint REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT pk_fields_likes PRIMARY KEY (film_id, user_id)
);

DROP TABLE IF EXISTS review CASCADE;
CREATE TABLE review
(
    review_Id   bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id     bigint REFERENCES films (film_id) ON DELETE CASCADE,
    user_id     bigint REFERENCES users (user_id) ON DELETE Cascade,
    content     varchar(400),
    isPositive  boolean DEFAULT true,
    useful      integer DEFAULT 0,
    review_date datetime
        CONSTRAINT text_not_blank_fields CHECK (content <> '')
);

DROP TABLE IF EXISTS events CASCADE;
CREATE TABLE events
(
    event_id   bigint GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    timestamp  bigint      NOT NULL,
    user_id    bigint REFERENCES users (user_id) ON DELETE CASCADE,
    entity_id  bigint      NOT NULL,
    event_type varchar(50) NOT NULL,
    operation  varchar(50) NOT NULL
);

CREATE INDEX IF NOT EXISTS film_name_index ON films (name);

CREATE INDEX IF NOT EXISTS users_email_index ON users (login);

CREATE INDEX IF NOT EXISTS users_name_index ON users (name);