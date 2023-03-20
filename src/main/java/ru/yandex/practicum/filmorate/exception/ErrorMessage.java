package ru.yandex.practicum.filmorate.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Builder
@AllArgsConstructor
@Getter
public class ErrorMessage {

    private int statusCode;
    private HttpStatus httpStatus;
    private Instant timeStamp;
    private String message;
    private String description;

}
