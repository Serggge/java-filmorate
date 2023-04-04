package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {

    private String message;
    private String description;

    public void setParams(String message, String description) {
        this.message = message != null ? message : "";
        this.description = description != null ? description : "";
    }

}
