package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.InMemoryFilmRepository;

import java.util.*;

@Service
@AllArgsConstructor
public class FilmService implements BaseService<Film> {
    private InMemoryFilmRepository inMemoryFilmRepository;

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
        return inMemoryFilmRepository.addLike(filmId, userId);
    }

    public boolean deleteLike(int filmId, int userId) {
        inMemoryFilmRepository.checkFilm(filmId);
        inMemoryFilmRepository.checkUserInFilmRepository(userId);
        return inMemoryFilmRepository.delete(filmId, userId);
    }

    public ArrayList<Film> getPopularFilms(int countFilm) {
        HashMap<Film, Integer> filmLikesCount = inMemoryFilmRepository.getMostPopular();

        List<Integer> likes = filmLikesCount
                .values()
                .stream()
                .sorted()
                .limit(countFilm)
                .toList();

        ArrayList<Film> mostPopularFilms = new ArrayList<>();
        for (Map.Entry<Film, Integer> filmWithLikes : filmLikesCount.entrySet()) {
            for (int like : likes) {
                if (filmWithLikes.getValue() == like) {
                    mostPopularFilms.add(filmWithLikes.getKey());
                }
            }
        }
        return mostPopularFilms;
    }
}
