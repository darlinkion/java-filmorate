package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryFilmRepository implements Repository<Film> {

    private final HashMap<Integer, Film> dataBase;
    private final HashMap<Integer, Set<Film>> filmsLikesDataBase;
    private int id = 0;

    @Override
    public Film create(Film film) {
        int tempId = generateId();
        film.setId(tempId);
        dataBase.put(tempId, film);
        return film;
    }

    @Override
    public Film update(Film temp) {
        int tempId = temp.getId();
        if (!dataBase.containsKey(tempId)) {
            throw new NotFoundException("Обновление невозможно пробемы с id " + temp);
        }
        log.info("Id при запроси у объекта" + tempId);
        dataBase.put(tempId, temp);
        return temp;
    }

    @Override
    public ArrayList<Film> getAll() {
        return new ArrayList<Film>(dataBase.values());
    }

    @Override
    public Film get(Integer fildId) {
        return dataBase.get(fildId);
    }

    private Integer generateId() {
        if (id < Integer.MAX_VALUE) {
            return ++id;
        }
        return -1;
    }
}
