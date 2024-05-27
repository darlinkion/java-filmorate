package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.InMemoryFilmRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService implements BaseService<Film> {
    private final InMemoryFilmRepository inMemoryFilmRepository;
    private final UserService userService;

    @Override
    public Film create(Film temp) {
        return inMemoryFilmRepository.create(temp);
    }

    @Override
    public Film update(Film temp) {
        return inMemoryFilmRepository.update(temp);
    }

    @Override
    public ArrayList<Film> getAll() {
        return inMemoryFilmRepository.getAll();
    }

    @Override
    public Film get(int id) {
        Optional<Film> tempFilm = Optional.ofNullable(inMemoryFilmRepository.get(id));
        if (!tempFilm.isPresent()) {
            throw new ValidationException("Нет такого фильма по id " + id);
        }
        return tempFilm.get();
    }

    public Film setLike(int filmId, int userId) {
        inMemoryFilmRepository.checkFilm(filmId);
        userService.checkUser(userId);
        return inMemoryFilmRepository.addLike(filmId, userId);
    }

    public boolean deleteLike(int filmId, int userId) {
        inMemoryFilmRepository.checkFilm(filmId);
        inMemoryFilmRepository.checkUserInFilmRepository(userId);
        return inMemoryFilmRepository.delete(filmId, userId);
    }

    public ArrayList<Film> getPopularFilms(int countFilm) {

        HashMap<Film, Integer> filmLikesCount = inMemoryFilmRepository.getMostPopular();

        List<Film> mostPopularFilms = filmLikesCount.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue() - entry1.getValue())
                .limit(countFilm)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return new ArrayList<>(mostPopularFilms);
    }
}
