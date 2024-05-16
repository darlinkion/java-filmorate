package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationExceptionInController;
import ru.yandex.practicum.filmorate.model.BaseModel;

import java.util.ArrayList;
import java.util.HashMap;


@Slf4j
public abstract class BaseController<T extends BaseModel> {

    private final HashMap<Integer, T> dataBase = new HashMap<>();
    @NotNull
    private Integer id = 0;

    public Integer generateId() {
        if (id < Integer.MAX_VALUE) {
            return ++id;
        }
        return -1;
    }

    public T create(T temp) {
        Integer tempId = generateId();
        temp.setId(tempId);
        dataBase.put(tempId, temp);
        return temp;
    }

    public T update(T temp) {
        Integer tempId = temp.getId();
        if (!dataBase.containsKey(tempId)) {
            throw new ValidationExceptionInController("Обновление невозможно пробемы с id " + temp);
        }
        log.info("Id при запроси у объекта" + tempId);
        dataBase.remove(tempId);
        dataBase.put(tempId, temp);
        return temp;
    }

    public ArrayList<T> getAll() {
        return new ArrayList<T>(dataBase.values());
    }
}
