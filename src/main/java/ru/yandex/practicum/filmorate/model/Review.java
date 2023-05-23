package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @NotNull
    private Long reviewId;
    @NotNull
    private Long filmId;
    @NotNull
    private Long userId;
    @NotBlank
    private String content;
    private Boolean isPositive;
    private Integer useful = 0;
    @PastOrPresent
    private LocalDateTime reviewDate;

}
