package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
@Setter
@NoArgsConstructor
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
