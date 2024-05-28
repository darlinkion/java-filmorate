package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserRepository implements Repository<User> {

    private final HashMap<Integer, User> dataBase;
    private int id = 0;

    @Override
    public User create(User film) {
        Integer tempId = generateId();
        film.setId(tempId);
        dataBase.put(tempId, film);
        return film;
    }

    @Override
    public User update(User temp) {
        Integer tempId = temp.getId();
        if (!dataBase.containsKey(tempId)) {
            throw new NotFoundException("Обновление невозможно пробемы с id " + temp);
        }
        log.info("Id при запроси у объекта" + tempId);
        dataBase.put(tempId, temp);
        return temp;
    }

    @Override
    public ArrayList<User> getAll() {
        return new ArrayList<User>(dataBase.values());
    }

    @Override
    public User get(Integer userId) {
        return dataBase.get(userId);
    }

    private Integer generateId() {
        if (id < Integer.MAX_VALUE) {
            return ++id;
        }
        return -1;
    }
}
