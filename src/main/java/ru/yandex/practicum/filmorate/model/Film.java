package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Film {

    private long id;
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
        this.duration = duration;
        this.releaseDate = validateReleaseDate(releaseDate);
    }

    @Builder
    public Film(long id, String name, String description, LocalDate releaseDate, int duration) {
        this(name, description, releaseDate, duration);
        this.id = id;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = validateReleaseDate(releaseDate);
    }

    private static LocalDate validateReleaseDate(LocalDate localDate) {
        if (localDate.isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза фильма раньше 28 декабря 1895 года");
        } else {
            return localDate;
        }
    }

}
