package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.BaseModel;

import java.util.ArrayList;

public interface BaseService<T extends BaseModel> {
    T create(T temp);

    T update(T temp);

    ArrayList<T> getAll();

    T get(int id);

}
