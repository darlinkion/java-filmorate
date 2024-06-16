package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.JdbcMpaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final JdbcMpaRepository jdbcMpaRepository;

    public List<Mpa> getAll() {
        return jdbcMpaRepository.getAll();
    }

    public Mpa get(int id) {
        if (id > 5) {
            throw new NotFoundException("Превышен максимально допустимый id для рейтинга");
        }
        return jdbcMpaRepository.get(id);
    }
}
