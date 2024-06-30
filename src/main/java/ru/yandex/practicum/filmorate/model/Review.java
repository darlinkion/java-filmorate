package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@EqualsAndHashCode(of = {"reviewId"})
@NoArgsConstructor
public class Review {
    int reviewId;
    @NotBlank
    String content;
    Boolean isPositive;
    @NotNull
    Integer userId;
    @NotNull
    Integer filmId;
    int useful = 0;
}
