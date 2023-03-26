package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@FieldDefaults
@AllArgsConstructor
@NoArgsConstructor
public class User {

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

}
