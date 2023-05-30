# java-filmorate
## Проект "Фильморейт" - рейтинг фильмов

История версий:

v.1.0 (release: 20/03/2023)
* Созданы модели данных Фильм и Пользователь
* Созданы REST API, добавлены контроллеры
* Реализована валидация для входных данных и логирование событий

v.1.1 (release: 04/04/2023)
* Доработана архитектура. Приложение разбито на слои: Controllers/Services/Storage
* Добавлена логика дружбы между пользователями
* Добавлена логика реакций пользователей на фильмы (возможность ставить лайки)

v.1.2 (release: 03/05/2023)
* Реализована сериализация/десериализация информации в базу данных:
    * разработана ER-диаграмма хранения информации в таблицах базы данных
    * реализованы DAO-классы и маппинг данных из таблиц
* Добавлены новые сущности - Жанр и Рейтинг Ассоциации Кинокомпаний (MPA) для фильмов
* Переработана логика добавления в друзья пользователей. Дружбу необходимо подтвердить другом пользователю

v.1.3 (release: 30/05/2023)
<br/>
<br/>
Расширена логика и функционал приложения:
* Функциональность «Отзывы». В приложении появились отзывы на фильмы. 
Добавленные отзывы имеют рейтинг и несколько дополнительных характеристик:
    * Оценка — полезно/бесполезно
    * Тип отзыва — негативный/положительный
    * У отзыва имеется рейтинг. При создании отзыва рейтинг равен нулю. 
Если пользователь оценил отзыв как полезный, это увеличивает его рейтинг на 1.
Если как бесполезный, то уменьшает на 1
* Функциональность «Поиск». Обеспечивает поиск по части названия фильма и по режиссёру
* Функциональность «Общие фильмы». Позволяет выводить общие с другом фильмы с сортировкой по их популярности
* Функциональность «Рекомендации». Реализована рекомендательная система для фильмов, предлагаемых пользователю
* Функциональность «Лента событий». Добавлена возможность просмотра последних событий на платформе для пользователя
* Функциональность «Фильмы по режиссёрам». Предполагает добавление к фильму информации о его режиссёре.
* Функциональность «Популярные фильмы». Предусматривает вывод самых любимых у зрителей фильмов по жанрам и годам.

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
* **mpa_id** - идентификатор рейтинга MPA (внешний ключ **mpa.mpa_id**)

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
* **name** - название рейтинга (*рейтинг международной ассоциации MPA, который характеризует доступность просмотра фильма для детей)
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

### Directors
Содержит данные о режиссёрах
<br/>
Таблица включает поля:
* первичный ключ **director_id** - идентификатор режиссёра
* **name** - имя и фамилия режиссёра

### Film_directors
Содержит информацию о том, какой фильм снял режиссёр
<br/>
Таблица включает поля:
* составной первичный ключ **film_id** - идентификатор фильма (внешний ключ **films.film_id**)
* составной первичный ключ **director_id** - идентификатор режиссёра (внешний ключ **directors.director_id**)

### Reviews
Содержит данные об отзывах к фильму, которые оставили пользователи
<br/>
Таблица включает поля:
* первичный ключ **review_id** - идентификатор отзыва
* **film_id** - идентификатор фильма (внешний ключ **films.film_id**)
* **user_id** - идентификатор пользователя (внешний ключ **users.user_id**)
* **content** - содержание отзыва, оставленного пользователем
* **isPositive** - маркер, определяющий, отрицательный или положительный был отзыв
* **useful** - определяет уровень полезности оставленного отзыва о фильме
* **review_date** - хранит информацию о дате, когда пользователем был оставлен данный отзыв

### Events
Хранит информацию о событиях на платформе для каждого пользователя
<br/>
Таблица включает поля:
* первичный ключ **event_id** - идентификатор события
* **timestamp** - дата и время, когда произошло событие
* **user_id** - идентификатор пользователя (внешний ключ **users.user_id**)
* **entity_id** - идентификатор сущности, с которой произошло событие
* **event_type** - тип события. Может хранить одно из следующих событий:
    * **LIKE** - реакция пользователей на фильм
    * **REVIEW** - действия с отзывами к фильмам
    * **FRIEND** - социальные взаимодействия пользователей
* **operation** - определяет операцию над событием и хранит одно из следующих значений:
    * **ADD** - добавление/создание
    * **UPDATE** - обновление
    * **REMOVE** - отмена/удаление


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
SELECT film_id,
      name,
      description,
      release_date,
      duration,
      mpa_id
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
SELECT user_id,
      friend_id,
      confirmed
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