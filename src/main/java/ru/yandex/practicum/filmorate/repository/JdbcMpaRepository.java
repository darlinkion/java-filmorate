package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcMpaRepository {
    private final JdbcTemplate jdbc;

    public static Mpa createMpa(ResultSet resultSet, int row) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(resultSet.getInt("RATING_ID"));
        mpa.setName(resultSet.getString("RATING_TITLE"));
        return mpa;
    }

    public List<Mpa> getAll() {
        return jdbc.query("SELECT * FROM RATING", JdbcMpaRepository::createMpa);
    }

    public Mpa get(int id) {
        List<Mpa> mpaList = jdbc.query("SELECT * FROM RATING WHERE RATING_ID=?",
                JdbcMpaRepository::createMpa, id);
        if (mpaList.size() != 1) {
            throw new EntityNotFoundException("Нет рейтинга по такому id: " + id);
        }
        Mpa mpa = mpaList.getFirst();
        log.info("Найден рейтинг: {}", mpa);
        return mpa;
    }
}