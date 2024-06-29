package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.type.EventType;
import ru.yandex.practicum.filmorate.model.type.OperationType;
import ru.yandex.practicum.filmorate.repository.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService implements BaseService<Film> {
    private final JdbcLikesRepository likesRepository;
    private final JdbcFilmIRepository jdbcFilmIRepository;
    private final JdbcGenreRepository jdbcGenreRepository;
    private final JdbcMpaRepository jdbcMpaRepository;
    private final JdbcUserRepository jdbcUserRepository;
    private final JdbcEventRepository jdbcEventRepository;

    @Override
    public Film create(Film film) {
        checkRatingAndGenres(film);
        return jdbcFilmIRepository.create(film);
    }

    @Override
    public Film update(Film film) {
        checkRatingAndGenres(film);
        get(film.getId());
        return jdbcFilmIRepository.update(film);
    }

    @Override
    public List<Film> getAll() {
        return jdbcFilmIRepository.getAll();
    }

    @Override
    public Film get(int id) {
        Optional<Film> tempFilm = Optional.ofNullable(jdbcFilmIRepository.get(id));
        if (tempFilm.isEmpty()) {
            throw new NotFoundException("Нет такого фильма по id " + id);
        }
        return tempFilm.get();
    }

    public void deleteById(int id) {
        if (get(id) == null) {
            throw new NotFoundException("Нет такого фильма по id " + id);
        }
        jdbcFilmIRepository.deleteById(id);
    }

    public void setLike(int filmId, int userId) {
        Film film = get(filmId);
        User user = jdbcUserRepository.get(userId);
        likesRepository.setLike(film.getId(), user.getId());
        jdbcEventRepository.addEvent(new Event(Instant.now().toEpochMilli(), userId, EventType.LIKE,
                OperationType.ADD, filmId));
    }

    public void deleteLike(int filmId, int userId) {
        likesRepository.deleteLike(filmId, userId);
        jdbcEventRepository.addEvent(new Event(Instant.now().toEpochMilli(), userId, EventType.LIKE,
                OperationType.REMOVE, filmId));
    }

    public List<Film> getPopularFilms(int countFilm, Integer genreId, Integer year) {
        List<Film> films = new ArrayList<>();
        if (genreId == null && year == null)
            return jdbcFilmIRepository.getPopularFilms(countFilm);
        if (year == null)
            return jdbcFilmIRepository.getPopularFilmsWithGenre(countFilm, genreId);

        if (genreId == null) {
            return jdbcFilmIRepository.getPopularFilmsWithYear(countFilm, year);
        } else {
            films = jdbcFilmIRepository.getPopularFilmsWithYearAndGenre(countFilm, genreId, year);
        }
        return films;
    }

    public List<Film> getMutualFilms(int userId, int friendId) {
        jdbcUserRepository.get(userId);
        jdbcUserRepository.get(friendId);
        return jdbcFilmIRepository.getMutualFilms(userId, friendId);
    }

    private void checkRatingAndGenres(Film film) {
        jdbcMpaRepository.get(film.getMpa().getId());
        Set<Genre> genres = film.getGenres();

        if (genres != null) {
            for (Genre genre : genres) {
                int genreId = genre.getId();
                jdbcGenreRepository.get(genreId);
            }
        }
    }

    public List<Film> getAllDirectorsFilms(int directorId, String sortType) {
        List<Film> films = jdbcFilmIRepository.getAllDirectorsFilms(directorId, sortType);
        return films;
    }
}
