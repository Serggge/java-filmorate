package ru.yandex.practicum.filmorate.model.user;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import javax.validation.constraints.*;

@Data
//@Builder
public class User {

    private int id;
    @Email
    @NotEmpty
    private String email;
    @NotBlank
    @Pattern(regexp = "^[^ ]+$")
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}
