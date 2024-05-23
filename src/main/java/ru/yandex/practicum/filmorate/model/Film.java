package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDate;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Builder
public class Film extends BaseModel {
    @NotBlank
    private String name;
    @NotBlank
    @Size(min = 3, max = 200)
    private String description;

    @FilmReleaseDate
    private LocalDate releaseDate;
    @NotNull
    @Positive
    private Integer duration;
}
