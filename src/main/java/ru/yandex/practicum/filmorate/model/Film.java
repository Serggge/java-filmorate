package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@FieldDefaults
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    long id;
    @NotBlank()
    String name;
    @Size(max = 200)
    @NotNull
    String description;
    @NotNull
    LocalDate releaseDate;
    @Positive
    int duration;

}
