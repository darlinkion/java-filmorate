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
public class DirectorService {

    private final JdbcDirectorRepository jdbcDirectorRepository;

    public List<Director> getAllDirectors() {
        return jdbcDirectorRepository.getAll();
    }

    public Director get(int id) {
    }

    public Director create(Director director) {
        return jdbcDirectorRepository.create(director);
    }


    public Director update(Director director) {
        get(director.getId());
        return jdbcDirectorRepository.update(director);
    }

    public void deleteDirector(int directorId) {
        get(directorId);
        jdbcDirectorRepository.deletDirector(directorId);
    }
}