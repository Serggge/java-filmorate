package ru.yandex.practicum.filmorate.model;

public enum MovieGenre {

    COMEDY("Комедия"),
    DRAMA("Драма"),
    CARTOON("Мультфильм"),
    THRILLER("Триллер"),
    DOCUMENTARY("Документальный"),
    ACTION("Боевик");

    private final String name;

    private MovieGenre(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
