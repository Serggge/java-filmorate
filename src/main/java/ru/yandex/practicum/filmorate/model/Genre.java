package ru.yandex.practicum.filmorate.model;

public enum Genre {

    COMEDY (1),
    DRAMA (2),
    CARTOON (3),
    THRILLER (4),
    DOCUMENTARY (5),
    ACTION (6);

    private final int id;
    private String name;

    private Genre(int id) {
        this.id = id;
    }

}
