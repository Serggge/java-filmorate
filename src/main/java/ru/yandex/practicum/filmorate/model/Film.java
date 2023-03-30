package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
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

    public List<Long> getLikes() {
        return new ArrayList<>(likes);
    }

    public void addLike(long id) {
        likes.add(id);
    }

    public boolean removeLike(long id) {
        return likes.remove(id);
    }

    public void clearLikes() {
        likes.clear();
    }

}
