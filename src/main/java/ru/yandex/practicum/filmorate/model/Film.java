package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@FieldDefaults(makeFinal=false, level=AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    final Set<Long> likes = new HashSet<>();
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
