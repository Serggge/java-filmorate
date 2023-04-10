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
public class User implements Cloneable {

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

    @Override
    public User clone() {
        try {
            return (User) super.clone();
        } catch (CloneNotSupportedException e) {
            User copy = new User(id, email, login, name, birthday);
            copy.friends.addAll(friends);
            return copy;
        }
    }

}
