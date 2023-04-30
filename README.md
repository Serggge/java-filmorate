# java-filmorate
## Проект "Фильморейт" - рейтинг фильмов

### ЕR-диаграмма
![project ER-diagram](src/main/resources/er_diagram.jpg)

## Описание диаграммы

### Users
Содержит данные о пользователях
<br/>
Таблица включает поля:
* первичный ключ **user_id** - идентификатор пользователя
* **login** - логин пользователя
* **email** - адрес электронной почты пользователя
* **name** - имя пользователя
* **birthday** - день рождения пользователя

### Films
Содержит данные о фильмах
<br/>
Таблица включает поля:
* первичный ключ **film_id** - идентификатор фильма
* **name** - название фильма
* **description** - описание фильма
* **release_date** - дата выхода фильма
* **duration** - продолжительность фильма
* **mpa_id** - идентификатор рейтинга MPA (внешний ключ **films.mpa_id**)

### Likes
Содержит информацию о реакциях пользователей к фильму
<br/>
Таблица включает поля:
* составной первичный ключ **film_id** - идентификатор фильма (внешний ключ **films.film_id**)
* составной первичный ключ **user_id** - идентификатор пользователя, который поставил лайк фильму 
(внешний ключ **users.user_id**)

### Friends
Содержит информацию о друзьях пользователей
<br/>
Таблица включает поля:
* составной первичный ключ **user_id** - идентификатор пользователя (внешний ключ **users.user_id**)
* составной первичный ключ **friend_id** - идентификатор друга пользователя (внешний ключ **users.user_id**)
* **confirmed** - статус подтвеждения запроса на дружбу

### Genres
Содержит данные о жанрах фильмов
<br/>
Таблица включает поля:
* первичный ключ **genre_id** - идентификатор жанра
* **name** - название жанра

### Mpa
Содержит данные о рейтингах MPA, определяющих возрастное ограничение
<br/>
Таблица включает поля:
* первичный ключ **mpa_id** - идентификатор рейтинга
* **name** - название рейтинга - (*рейтинг международной ассоциации MPA, который характеризует доступность просмотра фильма для детей)
    * **G** - у фильма нет возрастных ограничений
    * **PG** — детям рекомендуется смотреть фильм с родителями
    * **PG-13** — детям до 13 лет просмотр не желателен
    * **R** — лицам до 17 лет просматривать фильм можно только в присутствии взрослого
    * **NC-17** — лицам до 18 лет просмотр запрещён

### Film_genre
Содержит информацию к каким жанрам относится фильм
<br/>
Таблица включает поля:
* составной первичный ключ **film_id** - идентификатор фильма (внешний ключ **films.film_id**)
* составной первичный ключ **genre_id** - идентификатор жанра (внешний ключ **genres.genre_id**)


## Примеры запросов к таблицам, используемые в коде:
Добавление новой записи в таблицу films:
```
INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES (:name, :description, :releaseDate, :duration, :mpaId)
```
Обновление записи в таблице films:
```
UPDATE films
SET name = :name,
    description = :description,
    release_date = :releaseDate,
    duration = :duration,
    mpa_id = :mpaId
WHERE film_id = :id
```
Поиск фильма по идентификатору:
```
SELECT *
FROM films 
WHERE film_id = ?
```
Поиск идентификаторов друзей пользователя по его id:
```
SELECT friend_id
FROM friends
WHERE user_id = :id
UNION
SELECT user_id
FROM friends
WHERE friend_id = :id
  AND confirmed = TRUE
```
Поиск дружбы пользователей:
```
SELECT *
FROM friends
WHERE (user_id = :userId
       AND friend_id = :friendId)
  OR (user_id = :friendId
      AND friend_id = :userId)
```
Запрос для проверки статуса дружбы:
```
SELECT confirmed
FROM friends
WHERE user_id = :userId
  AND friend_id = :friendId
  OR user_id = :friendId
  AND friend_id = :userId
```
Запрос по изменению статуса дружбы с "неподтверждена" на "принята":
```
UPDATE friends
SET confirmed = TRUE
WHERE (user_id = :userId
       AND friend_id = :friendId)
  OR (user_id = :friendId
      AND friend_id = :userId)
```
Запрос по поиску идентификаторов пользователей, которые поставили лайк фильму:
```
SELECT user_id
FROM likes
WHERE film_id = ?
```