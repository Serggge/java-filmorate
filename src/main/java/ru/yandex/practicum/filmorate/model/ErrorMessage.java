package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import java.time.Instant;

@Builder
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
@Getter
@AllArgsConstructor
public class ErrorMessage {

    int statusCode;
    HttpStatus httpStatus;
    Instant timeStamp;
    String message;
    String description;

}
