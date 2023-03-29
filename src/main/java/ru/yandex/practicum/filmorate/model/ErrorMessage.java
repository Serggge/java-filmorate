package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import java.time.Instant;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {

    int statusCode;
    HttpStatus httpStatus;
    Instant timeStamp;
    String message;
    String description;

}
