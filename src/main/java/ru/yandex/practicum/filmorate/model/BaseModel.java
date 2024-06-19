package ru.yandex.practicum.filmorate.model;

import lombok.*;

@ToString
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
public abstract class BaseModel {
    protected Integer id;
}
