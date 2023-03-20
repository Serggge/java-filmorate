package ru.yandex.practicum.filmorate.model;

import lombok.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    private long id;
    @NotBlank()
    private String name;
    @Size(max = 200)
    @NotNull
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive
    private int duration;

}
