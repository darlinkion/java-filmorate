package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
            throw new ValidationException("Обновление невозможно пробемы с id " + temp);
        }
        log.info("Id при запроси у объекта" + tempId);
        dataBase.remove(tempId);
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

    @Override
    public boolean delete(int filmId, int userId) {
        Film tempFilm = dataBase.get(filmId);
        if (tempFilm == null) {
            throw new ValidationException("Такого фильма не существует");
        }
        return filmsLikesDataBase.get(userId).remove(tempFilm);
    }

    public Film addLike(int filmId, int userId) {
        Film tempFilm = dataBase.get(filmId);
        if (tempFilm == null) {
            throw new ValidationException("Невозможно поставить лайк, пробемы с id " + filmId);
        }
        log.info("idUser который ставик лайкт" + userId);
        Set<Film> userFilms = filmsLikesDataBase.get(userId);

        if (userFilms == null) {
            userFilms = new HashSet<Film>();
            userFilms.add(tempFilm);
            filmsLikesDataBase.put(userId, userFilms);

        } else {
            userFilms.add(tempFilm);
            filmsLikesDataBase.remove(userId);
            filmsLikesDataBase.put(userId, userFilms);
        }
        return tempFilm;
    }

    public HashMap<Film, Integer> getMostPopular() {
        HashMap<Film, Integer> mostPopularFilms = new HashMap<>();
        for (Set<Film> userFilms : filmsLikesDataBase.values()) {
            for (Film film : userFilms) {
                mostPopularFilms.put(film, mostPopularFilms.getOrDefault(film, 0) + 1);
            }
        }
        return new HashMap<>(mostPopularFilms);
    }

    public void checkFilm(int id) {
        Film tempFilm = dataBase.get(id);
        if (tempFilm == null) {
            throw new ValidationException("Нет фильма с таким id " + id);
        }
    }

    public void checkUserInFilmRepository(int id) {
        Set<Film> films = filmsLikesDataBase.get(id);
        if (films == null) {
            throw new ValidationException("Нет юзера с таким id " + id);
        }
    }

    private Integer generateId() {
        if (id < Integer.MAX_VALUE) {
            return ++id;
        }
        return -1;
    }


}
