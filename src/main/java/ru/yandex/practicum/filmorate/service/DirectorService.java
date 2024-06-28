package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.JdbcFilmIRepository;
import ru.yandex.practicum.filmorate.repository.JdbcGenreRepository;
import ru.yandex.practicum.filmorate.repository.JdbcLikesRepository;
import ru.yandex.practicum.filmorate.repository.JdbcMpaRepository;
import ru.yandex.practicum.filmorate.repository.JdbcDirectorRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DirectorService implements BaseService<Film> {
    private final JdbcLikesRepository likesRepository;
    private final JdbcFilmIRepository jdbcFilmIRepository;
    private final JdbcGenreRepository jdbcGenreRepository;
    private final JdbcMpaRepository jdbcMpaRepository;


    public List<Director> getAllDirectors() {
        List<Director> directors = jdbcDirectorRepository.getAll();
        return films;
    }

    public Film get(int id) {
        Optional<Director> tempDirector = Optional.ofNullable(jdbcDirectorRepository.get(id));
        if (tempDirector.isEmpty()) {
            throw new NotFoundException("Нет такого режисера по id " + id);
        }
        return tempDirector.get();
    }

    @Override
    public Director create(Director director) {
        return jdbcDirectorRepository.create(director);
    }

    @Override
    public Director update(Director director) {
        get(director.getId());
        return jdbcDirectorRepository.update(director);
    }

    public void deleteDirector(int directorId) {
        get(userId);
        jdbcUserRepository.deleteDirector(userId);
    }
}
