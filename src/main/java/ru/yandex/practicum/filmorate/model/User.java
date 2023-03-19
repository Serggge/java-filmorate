package ru.yandex.practicum.filmorate.model;

import lombok.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class User {

    private long id;
    @Email
    @NotEmpty
    private String email;
    @NotBlank
    @Pattern(regexp = "^[^ ]+$")
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday;

    @Builder
    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        this.name = name;
        validate();
    }

    @Builder
    public User(long id, String email, String login, String name, LocalDate birthday) {
        this(email, login, name, birthday);
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
        validate();
    }

    public void validate() {
        if (name == null || name.isBlank()) {
            name = login;
        }
    }

}
