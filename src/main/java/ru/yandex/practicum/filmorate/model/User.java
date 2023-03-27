package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@FieldDefaults(makeFinal=false, level=AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class User {

    final Set<Long> friends = new HashSet<>();
    long id;
    @Email
    @NotEmpty
    String email;
    @NotBlank
    @Pattern(regexp = "^[^ ]+$")
    String login;
    String name;
    @NotNull
    @PastOrPresent
    LocalDate birthday;

    public void addFriendId(Long id) {
        friends.add(id);
    }

}
