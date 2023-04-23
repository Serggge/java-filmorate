package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Mpa {

    private long id;
    private MpaRating name;

    public String getName() {
        return name.toString();
    }

}
