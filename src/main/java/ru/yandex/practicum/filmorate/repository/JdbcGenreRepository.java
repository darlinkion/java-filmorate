package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcGenreRepository {
    private final JdbcTemplate jdbc;

    public static Genre createGenre(ResultSet resultSet, int row) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getInt("GENRE_ID"));
        genre.setName(resultSet.getString("GENRE_NAME"));

        return genre;
    }

    public List<Genre> getAll() {
        return jdbc.query("SELECT * FROM GENRE", JdbcGenreRepository::createGenre);
    }

    public Genre get(int id) {
        List<Genre> genreList = jdbc.query("SELECT * FROM GENRE WHERE GENRE_ID=?",
                JdbcGenreRepository::createGenre, id);
        if (genreList.size() != 1) {
            throw new EntityNotFoundException("Нет жанра по такому id: " + id);
        }
        Genre genre = genreList.getFirst();
        log.info("Найден жанр: {}", genre);
        return genre;
    }
}
