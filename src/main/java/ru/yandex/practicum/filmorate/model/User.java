package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.exception.DataUpdateException;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class User {

    //final Map<Long, Boolean> friends = new HashMap<>();
    long id;
    @NotBlank @Pattern(regexp = "^[^ ]+$", message = "логин не может содержать пробелы")
    String login;
    @Email @NotEmpty
    String email;
    String name;
    @NotNull @PastOrPresent LocalDate birthday;

/*    public void addFriendId(long id) {
        if (friends.get(id) == null) {
            friends.put(id, Boolean.FALSE);
        } else if (friends.get(id).equals(Boolean.FALSE)){
            friends.put(id, Boolean.TRUE);
        } else {
            throw new DataUpdateException(String.format(
                    "Пользователь с id=%d уже в друзьях у пользователя с id=%d", id, this.id));
        }
    }

    public boolean deleteFriendId(long id) {
        return friends.remove(id);
    }

    public List<Long> getFriends() {
        return new ArrayList<>(friends.keySet());
    }

    public void clearFriendList() {
        friends.clear();
    }*/

}
