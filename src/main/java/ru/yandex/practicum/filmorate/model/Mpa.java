package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class Mpa extends BaseModel {
    private String name;
}
