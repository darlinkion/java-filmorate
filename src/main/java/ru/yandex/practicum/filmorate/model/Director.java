package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Director extends BaseModel {
    @NotBlank
    @Size(min = 1, max=1000, message = "Name not be empty")
    private String name;
}