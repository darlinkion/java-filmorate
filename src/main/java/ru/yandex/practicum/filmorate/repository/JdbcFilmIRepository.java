package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcFilmIRepository implements IRepository<Film> {
    private final JdbcTemplate jdbc;

    private static Film createFilm(ResultSet resultSet, int row) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("FILM_ID"));
        film.setName(resultSet.getString("NAME"));
        film.setDescription(resultSet.getString("DESCRIPTION"));
        film.setReleaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate());
        film.setDuration(resultSet.getInt("DURATION"));
        film.setMpa(new Mpa());
        film.getMpa().setId(resultSet.getInt("RATING_ID"));
        film.getMpa().setName(resultSet.getString("RATING_TITLE"));
        return film;
    }

    @Override
    public Film create(Film film) {
        String sqlQuery = "INSERT INTO FILM (NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) VALUES(?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"FILM_ID"});
            preparedStatement.setString(1, film.getName());
            preparedStatement.setString(2, film.getDescription());
            preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
            preparedStatement.setInt(4, film.getDuration());
            preparedStatement.setInt(5, film.getMpa().getId());
            return preparedStatement;
        }, keyHolder);

        Integer filmId = keyHolder.getKeyAs(Integer.class);
        film.setId(filmId);

        if (filmId == null) {
            throw new NotFoundException("Фильм не добавлен");
        }

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String genreSql = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES(?, ?)";
                jdbc.update(genreSql, filmId, genre.getId());
            }
        }

        log.info("Фильм с идентификатором {} добавлен.", filmId);
        return film;
    }

    @Override
    public Film update(Film film) {
        int id = film.getId();
        jdbc.update("UPDATE FILM SET  NAME=?, DESCRIPTION=?, RELEASE_DATE=?, DURATION=?, RATING_ID=? " +
                        "WHERE FILM_ID=?;",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                id);

        jdbc.update("DELETE FROM FILM_GENRE WHERE FILM_ID=?;", id);

        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                jdbc.update("INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES(?, ?)", id, genre.getId());
            }
        }
        return film;
    }

    @Override
    public List<Film> getAll() {
        List<Film> list = jdbc.query("SELECT F.FILM_ID," +
                        " F.NAME," +
                        " F.DESCRIPTION," +
                        " F.RELEASE_DATE," +
                        " F.DURATION," +
                        " F.RATING_ID," +
                        " R.RATING_TITLE " +
                        "FROM FILM AS F " +
                        "INNER JOIN RATING AS R ON F.RATING_ID = R.RATING_ID;",
                JdbcFilmIRepository::createFilm);
        for (Film film : list) {
            genresForFilm(film);
        }
        return list;
    }

    @Override
    public Film get(int id) {
        String sqlQuery = "SELECT F.FILM_ID," +
                " F.NAME," +
                " F.DESCRIPTION," +
                " F.RELEASE_DATE," +
                " F.DURATION," +
                " F.RATING_ID," +
                " R.RATING_TITLE " +
                "FROM FILM AS F " +
                "INNER JOIN RATING AS R ON F.RATING_ID = R.RATING_ID " +
                "WHERE F.FILM_ID = ?;";
        List<Film> films = jdbc.query(sqlQuery, JdbcFilmIRepository::createFilm, id);
        if (films.size() != 1) {
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new NotFoundException("Пользователь с идентификатором id не найден.");
        }
        log.info("Найден фильм: {} {}", id, films.getFirst().getName());
        genresForFilm(films.getFirst());
        return films.getFirst();
    }

    private Film genresForFilm(Film film) {
        String sqlQueryGenre = "SELECT G.GENRE_ID, G.GENRE_NAME " +
                "FROM FILM_GENRE AS FG " +
                "INNER JOIN GENRE AS G ON FG.GENRE_ID = G.GENRE_ID " +
                "WHERE FG.FILM_ID = ?;";
        LinkedHashSet<Genre> set = new LinkedHashSet<>(jdbc.query(sqlQueryGenre,
                JdbcGenreRepository::createGenre, film.getId()));
        if (!set.isEmpty()) {
            film.setGenres(set);
        } else {
            film.setGenres(new LinkedHashSet<>());
        }
        return film;
    }

    public List<Film> getPopularFilms(int countFilm) {

        List<Film> list = jdbc.query("SELECT F.FILM_ID, " +
                "F.NAME, " +
                "F.DESCRIPTION, " +
                "F.RELEASE_DATE, " +
                "F.DURATION, " +
                "F.RATING_ID, " +
                "R.RATING_TITLE, " +
                "COUNT(L.FILM_ID) " +
                "FROM FILM AS F " +
                "LEFT JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID " +
                "INNER JOIN RATING AS R ON F.RATING_ID = R.RATING_ID " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(L.FILM_ID) DESC " +
                "LIMIT ?;", JdbcFilmIRepository::createFilm, countFilm);
        return list;
    }
}
