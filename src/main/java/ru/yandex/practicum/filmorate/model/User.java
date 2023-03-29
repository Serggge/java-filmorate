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
@AllArgsConstructor
@NoArgsConstructor
public class User {

    final Set<Long> friends = new HashSet<>();
    long id;
    @Email
    @NotEmpty
    String email;
    @NotBlank
    @Pattern(regexp = "^[^ ]+$", message = "логин не может содержать пробелы")
    String login;
    String name;
    @NotNull
    @PastOrPresent
    LocalDate birthday;

    public void addFriendId(long id) {
        friends.add(id);
    }

    public boolean deleteFriendId(long id) {
        return friends.remove(id);
    }

    public List<Long> getFriends() {
        return new ArrayList<>(friends);
    }

    public void clearFriendList() {
        friends.clear();
    }

}
