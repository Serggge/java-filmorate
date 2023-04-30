package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.yandex.practicum.filmorate.serialize.MpaDeserializer;

@JsonDeserialize(using = MpaDeserializer.class)
@Getter
@EqualsAndHashCode(exclude = "name")
public class Mpa {

    private int id;
    private String name;

    public Mpa(int id) {
        this.id = id;
        this.name = MpaRating.values()[id - 1].getName();
    }

    public void setId(int id) {
        this.id = id;
        this.name = MpaRating.values()[id - 1].getName();
    }

}
