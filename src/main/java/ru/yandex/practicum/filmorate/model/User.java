package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class User {

    final Set<Long> friends = new HashSet<>();
    long id;
    @NotBlank @Pattern(regexp = "^[^ ]+$", message = "логин не может содержать пробелы")
    String login;
    @Email @NotEmpty
    String email;
    String name;
    @NotNull @PastOrPresent LocalDate birthday;

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
