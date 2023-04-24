package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre {

    private int id;
    private String name;

    public Genre(int id) {
        this.id = id;
        this.name = MovieGenre.values()[id - 1].getName();
    }

}
