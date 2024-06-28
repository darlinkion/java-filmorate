package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.BaseModel;

import java.util.List;

public interface IRepository<T extends BaseModel> {

    T create(T data);

    T update(T data);

    List<T> getAll();

    T get(int id);
}
