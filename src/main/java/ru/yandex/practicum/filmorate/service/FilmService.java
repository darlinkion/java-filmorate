package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.InMemoryFilmRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
        if (tempFilm.isEmpty()) {
            throw new NotFoundException("Нет такого фильма по id " + id);
        }
        return tempFilm.get();
    }


    public Film setLike(int filmId, int userId) {
        userService.get(userId);
        Film film = get(filmId);
        film.getLikes().add(userId);
        return film;
    }

    public Film deleteLike(int filmId, int userId) {
        userService.get(userId);
        Film film = get(filmId);
        film.getLikes().remove(userId);
        return film;
    }

    public List<Film> getPopularFilms(int countFilm) {
        List<Film> tempList = inMemoryFilmRepository.getAll();
        if (tempList == null) {
            return List.of();
        }

        return tempList
                .stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(countFilm)
                .collect(Collectors.toList());
    }
}
