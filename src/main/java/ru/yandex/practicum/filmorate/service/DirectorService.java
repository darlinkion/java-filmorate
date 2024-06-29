package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.JdbcDirectorRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final JdbcDirectorRepository jdbcDirectorRepository;

    public List<Director> getAllDirectors() {
        return jdbcDirectorRepository.getAll();
    }

    public Director get(int id) {
        Director tempDirector = jdbcDirectorRepository.get(id);
        return tempDirector;
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
        jdbcDirectorRepository.deleteDirector(directorId);
    }
}