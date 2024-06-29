package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcDirectorRepository {
    private final JdbcTemplate jdbc;

    public static Director createDirector(ResultSet resultSet, int row) throws SQLException {
        Director director = new Director();
        director.setId(resultSet.getInt("DIRECTOR_ID"));
        director.setName(resultSet.getString("DIRECTOR_NAME"));
        return director;
    }

    public Director create(Director director) {
        String sqlQuery = "INSERT INTO DIRECTORS (DIRECTOR_NAME) VALUES(?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"DIRECTOR_ID"});
            preparedStatement.setString(1, director.getName());
            return preparedStatement;
        }, keyHolder);

        Integer directorId = keyHolder.getKeyAs(Integer.class);
        director.setId(directorId);
        if (directorId == null) {
            throw new NotFoundException("Режиссер не добавлен");
        }
        log.info("Режиссер с идентификатором {} добавлен.", directorId);
        return director;
    }

    public List<Director> getAll() {
        return jdbc.query("SELECT * FROM DIRECTORS", JdbcDirectorRepository::createDirector);
    }

    public Director get(int id) {
        List<Director> directorsList = jdbc.query("SELECT DIRECTOR_ID, DIRECTOR_NAME FROM DIRECTORS WHERE DIRECTOR_ID=?",
                JdbcDirectorRepository::createDirector, id);
        if (directorsList.size() != 1) {
            log.info("Режисер с идентификатором {} не найден.", id);
            throw new NotFoundException("Нет режисера по такому id: " + id);
        }
        Director director = directorsList.getFirst();
        log.info("Найден режисер: {}", director);
        return director;
    }

    public Director update(Director director) {
        int id = director.getId();
        jdbc.update("UPDATE DIRECTORS SET DIRECTOR_NAME=? WHERE DIRECTOR_ID=?;",
                director.getName(), id);
        return director;
    }

    public void deletDirector(int directorId) {
        jdbc.update("DELETE FROM DIRECTORS WHERE DIRECTOR_ID=?", directorId);
    }
}