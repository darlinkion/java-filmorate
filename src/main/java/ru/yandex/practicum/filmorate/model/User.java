package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Builder
public class User extends BaseModel {
    @Email
    private String email;
    @NotBlank
    private String login;
    private String name;
    @Past
    @NotNull
    private LocalDate birthday;
}
