insert into genre (genre_id, name)
values (1, 'Comedy'), (2, 'Drama'), (3, 'Cartoon'), (4, 'Thriller'), (5, 'Documentary'), (6, 'Action');

insert into rating (rating_id, name)
values (1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17');

insert into film (film_id, name, description, release_date, duration, rating)
values (1, 'The Shawshank Redemption', 'Over the course of several years, two convicts form a friendship, seeking consolation and, eventually, redemption through basic compassion.', '1994-09-10', 142, 4),
(2, 'The Godfather', 'The aging patriarch of an organized crime dynasty in postwar New York City transfers control of his clandestine empire to his reluctant youngest son.', '1972-03-14', 175, 4),
(3, 'The Dark Knight', 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.', '2008-07-14', 152, 3),
(4, 'Schindler''s List', 'In German-occupied Poland during World War II, industrialist Oskar Schindler gradually becomes concerned for his Jewish workforce after witnessing their persecution by the Nazis.', '1993-11-30', 195, 4),
(5, 'The Lord of the Rings', 'Gandalf and Aragorn lead the World of Men against Sauron''s army to draw his gaze from Frodo and Sam as they approach Mount Doom with the One Ring.', '2003-12-01', 201, 3),
(6, 'Star Wars', 'Luke Skywalker joins forces with a Jedi Knight, a cocky pilot, a Wookiee and two droids to save the galaxy from the Empire''s world-destroying battle station, while also attempting to rescue Princess Leia from the mysterious Darth Vader.', '1977-05-25', 121, 2);

insert into film_genre (film_id, genre_id)
values (1, 2), (2, 2), (3, 2), (3, 4), (3, 6), (4, 2), (5, 2), (5, 6), (6, 6);

insert into users (user_id, email, name, birthday)
values (1, 'sergey@yandex.ru', 'Sergey', '1984-01-14'),
(2, 'ivan@gmail.com', 'Ivan', '1990-02-10'),
(3, 'petr@yahoo.com', 'Peter', '1995-07-06'),
(4, 'vladimir@mail.ru', 'Vladimir', '1998-11-25'),
(5, 'andrey@bk.org', 'Andrey', '2010-01-01');

insert into likes (film_id, user_id)
values (1, 1), (1, 2), (2, 1), (2, 2), (2, 3), (3, 1), (3, 3), (3, 4),
(4, 1), (4, 4), (4, 5), (5, 1), (5, 5), (6, 1);

insert into friends (user_id, friend_id, confirmed)
values (1, 2, true), (1, 3, false), (2, 3, true), (5, 3, true), (5, 1, false);