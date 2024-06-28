package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.BaseModel;

import java.util.List;

public interface BaseService<T extends BaseModel> {
    T create(T temp);

    T update(T temp);

    List<T> getAll();

    T get(int id);

}
