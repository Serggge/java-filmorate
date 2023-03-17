package ru.yandex.practicum.filmorate.model.film;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.*;

@Data
public class Film {

    private int id;
    @NotBlank()
    private String name;
    @Size(max = 200)
    @NotNull
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration;

    @Builder()
    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

}
