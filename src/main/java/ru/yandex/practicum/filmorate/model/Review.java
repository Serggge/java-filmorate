package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    private Long reviewId;
    private Long filmId;
    private Long userId;
    private String content;
    private Boolean isPositive;
    private Integer useful = 0;
    private LocalDateTime reviewDate;

}
