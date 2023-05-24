package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Film implements Comparable<Film> {

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
    @NotNull
    Mpa mpa;
    final Set<Genre> genres = new HashSet<>();
    final Set<Long> likes = new HashSet<>();

    public int getMpaId() {
        return mpa.getId();
    }

    public void addLike(long id) {
        likes.add(id);
    }

    public boolean removeLike(long id) {
        return likes.remove(id);
    }

    public int popularity() {
        return likes.size();
    }

    public void clearLikes() {
        likes.clear();
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    @Override
    public int compareTo(Film o) {
        return Long.compare(this.id, o.id);
    }

}
