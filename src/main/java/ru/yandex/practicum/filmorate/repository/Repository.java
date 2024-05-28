package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.BaseModel;

import java.util.ArrayList;

public interface Repository<T extends BaseModel> {

    T create(T data);

    T update(T data);

    ArrayList<T> getAll();

    T get(Integer id);
}
