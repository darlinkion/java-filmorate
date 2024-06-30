package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDate;

import java.time.LocalDate;
import java.util.Set;

@ToString
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Film extends BaseModel {
    @NotBlank
    private String name;
    @NotBlank
    @Size(min = 3, max = 200)
    private String description;
    @FilmReleaseDate
    private LocalDate releaseDate;
    @Positive
    private Integer duration;

    @NotNull
    private Mpa mpa;
    private Set<Genre> genres;
    private Set<Director> directors;
}
