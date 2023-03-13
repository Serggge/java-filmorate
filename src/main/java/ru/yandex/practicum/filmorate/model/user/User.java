package ru.yandex.practicum.filmorate.model.user;

import java.time.LocalDate;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.*;

@Data
public class User {

    private int id;
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "^[^ ]+$")
    private String login;
    private String name;
    @Past
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        ++id;
    }
}
