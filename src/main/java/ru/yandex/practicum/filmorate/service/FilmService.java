package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.JdbcFilmIRepository;
import ru.yandex.practicum.filmorate.repository.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.repository.JdbcLikesRepository;
import ru.yandex.practicum.filmorate.repository.JdbcMpaRepository;

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

    public void setLike(int filmId, int userId) {
        likesRepository.setLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        likesRepository.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int countFilm) {
        List<Film> films = jdbcFilmIRepository.getPopularFilms(countFilm);
        return films;
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
}
