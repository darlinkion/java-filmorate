package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcFilmRepository implements IRepository<Film> {
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

        addGenres(film.getGenres(), filmId);
        addDirectors(film.getDirectors(), filmId);
        log.info("Фильм с идентификатором {} добавлен.", filmId);
        return get(filmId);
    }

    @Override
    public Film update(Film film) {
        int id = film.getId();
        jdbc.update("UPDATE FILM SET NAME=?, DESCRIPTION=?, RELEASE_DATE=?, DURATION=?, RATING_ID=? " +
                        "WHERE FILM_ID=?;",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                id);

        jdbc.update("DELETE FROM FILM_GENRE WHERE FILM_ID=?;", id);

        Set<Genre> genres = film.getGenres();
        addGenres(genres, id);

        jdbc.update("DELETE FROM FILM_DIRECTORS WHERE FILM_ID=?;", id);

        Set<Director> directors = film.getDirectors();
        addDirectors(directors, id);

        return get(film.getId());
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
                JdbcFilmRepository::createFilm);

        return filmsWithGenres(filmsWithDirectors(list));
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
        List<Film> films = jdbc.query(sqlQuery, JdbcFilmRepository::createFilm, id);
        if (films.size() != 1) {
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new NotFoundException("Фильм с идентификатором id = " + id + " не найден.");
        }
        log.info("Найден фильм: {} {}", id, films.getFirst().getName());
        genresForFilm(films.getFirst());
        directorForFilm(films.getFirst());
        return films.getFirst();
    }

    @Override
    public void deleteById(int id) {
        try {
            String sql = "delete from film where film_id = ?";
            jdbc.update(sql, id);
        } catch (Exception e) {
            log.error("Ошибка в удалении фильма по идентификатору из БД: {}", e.getMessage(), e);
            throw new RuntimeException("Произошла ошибка при выполнении запроса", e);
        }
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
                "LIMIT ?;", JdbcFilmRepository::createFilm, countFilm);

        return filmsWithGenres(filmsWithDirectors(list));
    }

    public List<Film> getPopularFilmsWithGenre(int countFilm, int genreId) {

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
                "INNER JOIN FILM_GENRE AS FG ON F.FILM_ID = FG.FILM_ID " +
                "WHERE FG.GENRE_ID=? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(L.FILM_ID) DESC " +
                " LIMIT ?;", JdbcFilmRepository::createFilm, genreId, countFilm);

        return filmsWithGenres(filmsWithDirectors(list));
    }

    public List<Film> getPopularFilmsWithYear(int countFilm, int year) {

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
                "WHERE EXTRACT(YEAR FROM F.RELEASE_DATE)=?" +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(L.FILM_ID) DESC " +
                "LIMIT ?;", JdbcFilmRepository::createFilm, year, countFilm);

        return filmsWithGenres(filmsWithDirectors(list));
    }

    public List<Film> getPopularFilmsWithYearAndGenre(int countFilm, int genreId, int year) {

        List<Film> films = jdbc.query("SELECT F.FILM_ID, " +
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
                "INNER JOIN FILM_GENRE AS FG ON F.FILM_ID = FG.FILM_ID " +
                "WHERE FG.GENRE_ID=? AND EXTRACT(YEAR FROM F.RELEASE_DATE)=?" +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(L.FILM_ID) DESC " +
                "LIMIT ?;", JdbcFilmRepository::createFilm, genreId, year, countFilm);

        return filmsWithGenres(filmsWithDirectors(films));
    }

    public List<Film> getMutualFilms(int userID, int friendId) {
        String sql = "SELECT F.FILM_ID, " +
                "F.NAME, " +
                "F.DESCRIPTION, " +
                "F.RELEASE_DATE, " +
                "F.DURATION, " +
                "F.RATING_ID, " +
                "R.RATING_TITLE " +
                "FROM FILM AS F " +
                "INNER JOIN LIKES AS L1 ON F.FILM_ID = L1.FILM_ID " +
                "INNER JOIN LIKES AS L2 ON F.FILM_ID = L2.FILM_ID " +
                "INNER JOIN RATING AS R ON F.RATING_ID = R.RATING_ID " +
                "WHERE L1.USER_ID = ? AND L2.USER_ID = ? " +
                "ORDER BY F.FILM_ID;";

        List<Film> films = jdbc.query(sql, JdbcFilmRepository::createFilm, userID, friendId);

        return filmsWithGenres(filmsWithDirectors(films));
    }

    public List<Film> getAllDirectorsFilms(int directorId, String sortType) {

        String sql = "SELECT F.FILM_ID, " +
                "F.NAME, " +
                "F.DESCRIPTION, " +
                "F.RELEASE_DATE, " +
                "F.DURATION, " +
                "F.RATING_ID, " +
                "R.RATING_TITLE " +
                "FROM FILM AS F " +
                "INNER JOIN FILM_DIRECTORS AS FD ON F.FILM_ID = FD.FILM_ID " +
                "LEFT JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID " +
                "LEFT JOIN RATING AS R ON F.RATING_ID = R.RATING_ID " +
                "WHERE FD.DIRECTOR_ID = ? " +
                "GROUP BY F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.RATING_ID " +
                "ORDER BY ";

        if ("likes".equals(sortType)) {
            sql += "COUNT(L.FILM_ID) DESC;";
        } else if ("year".equals(sortType)) {
            sql += "EXTRACT(YEAR FROM F.RELEASE_DATE) ASC;";
        } else {
            throw new IllegalArgumentException("Недоступный тип сортировки: " + sortType);
        }

        List<Film> films = jdbc.query(sql, JdbcFilmRepository::createFilm, directorId);

        return filmsWithGenres(filmsWithDirectors(films));
    }

    public List<Film> getRecommendationFilms(long userId) {
        String sql = "SELECT * FROM FILM AS f " +
                "JOIN RATING AS r ON f.RATING_ID = r.RATING_ID " +
                "WHERE f.FILM_ID IN ( " +
                "SELECT FILM_ID FROM LIKES " +
                "WHERE USER_ID IN ( " +
                "SELECT l1.USER_ID FROM LIKES AS l1 " +
                "RIGHT JOIN LIKES AS l2 ON l2.FILM_ID = l1.FILM_ID " +
                "WHERE l1.USER_ID IS NOT NULL " +
                "AND l1.USER_ID != ? AND l2.USER_ID = ? " +
                "GROUP BY l1.USER_ID " +
                "ORDER BY COUNT(l1.USER_ID) DESC " +
                "LIMIT 10) " +
                "AND f.FILM_ID NOT IN (  " +
                "SELECT FILM_ID FROM LIKES " +
                "WHERE USER_ID = ? ));";

        List<Film> films = jdbc.query(sql, JdbcFilmRepository::createFilm, userId, userId, userId);

        return filmsWithGenres(filmsWithDirectors(films));
    }

    public List<Film> searchFilmsByTitle(String query) {
        String sql = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, " +
                "F.DURATION, F.RATING_ID, R.RATING_TITLE, COUNT(L.FILM_ID) " +
                "FROM FILM AS F " +
                "LEFT JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID " +
                "INNER JOIN RATING AS R ON F.RATING_ID = R.RATING_ID " +
                "LEFT JOIN FILM_DIRECTORS AS FD ON F.FILM_ID = FD.FILM_ID " +
                "LEFT JOIN DIRECTORS AS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "WHERE LOWER(F.NAME) LIKE ? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(L.FILM_ID) DESC";


        List<Film> searchFilms = jdbc.query(sql, JdbcFilmRepository::createFilm, query);

        return filmsWithGenres(filmsWithDirectors(searchFilms));
    }

    public List<Film> searchFilmsByDirector(String query) {
        String sql = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, " +
                "F.DURATION, F.RATING_ID, R.RATING_TITLE, COUNT(L.FILM_ID) " +
                "FROM FILM AS F " +
                "LEFT JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID " +
                "INNER JOIN RATING AS R ON F.RATING_ID = R.RATING_ID " +
                "LEFT JOIN FILM_DIRECTORS AS FD ON F.FILM_ID = FD.FILM_ID " +
                "LEFT JOIN DIRECTORS AS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "WHERE LOWER(D.DIRECTOR_NAME) LIKE ? " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(L.FILM_ID) DESC";


        List<Film> searchFilms = jdbc.query(sql, JdbcFilmRepository::createFilm, query);

        return filmsWithGenres(filmsWithDirectors(searchFilms));
    }

    public List<Film> searchFilmsByTitleAndDirector(String query) {
        String sql = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, " +
                "F.DURATION, F.RATING_ID, R.RATING_TITLE, COUNT(L.FILM_ID) " +
                "FROM FILM AS F " +
                "LEFT JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID " +
                "INNER JOIN RATING AS R ON F.RATING_ID = R.RATING_ID " +
                "LEFT JOIN FILM_DIRECTORS AS FD ON F.FILM_ID = FD.FILM_ID " +
                "LEFT JOIN DIRECTORS AS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "WHERE LOWER(F.NAME) LIKE ? OR LOWER(D.DIRECTOR_NAME) LIKE ?" +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(L.FILM_ID) DESC";

        List<Film> searchFilms = jdbc.query(sql, JdbcFilmRepository::createFilm, query, query);

        return filmsWithGenres(filmsWithDirectors(searchFilms));
    }

    private Film directorForFilm(Film film) {
        String sqlQuery = "SELECT D.DIRECTOR_ID, " +
                "D.DIRECTOR_NAME " +
                "FROM FILM_DIRECTORS AS FD " +
                "INNER JOIN DIRECTORS AS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                "WHERE FD.FILM_ID = ?;";

        LinkedHashSet<Director> directors = new LinkedHashSet<>(jdbc.query(sqlQuery,
                JdbcDirectorRepository::createDirector, film.getId()));

        film.setDirectors(directors);
        return film;
    }

    private Film genresForFilm(Film film) {
        String sqlQueryGenre = "SELECT G.GENRE_ID, G.GENRE_NAME " +
                "FROM FILM_GENRE AS FG " +
                "INNER JOIN GENRE AS G ON FG.GENRE_ID = G.GENRE_ID " +
                "WHERE FG.FILM_ID = ?;";

        LinkedHashSet<Genre> genres = new LinkedHashSet<>(jdbc.query(sqlQueryGenre,
                JdbcGenreRepository::createGenre, film.getId()));

        film.setGenres(genres);
        return film;
    }

    private List<Film> filmsWithDirectors(List<Film> films) {
        if (films.isEmpty()) {
            return films;
        }

        String sqlQuery = "SELECT FD.FILM_ID, D.DIRECTOR_ID, D.DIRECTOR_NAME " +
                "FROM FILM_DIRECTORS AS FD " +
                "INNER JOIN DIRECTORS AS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID;";

        Map<Integer, Set<Director>> directorsByFilmId = new HashMap<>();

        jdbc.query(sqlQuery, (rs) -> {
            int filmId = rs.getInt("FILM_ID");
            Director director = new Director();
            director.setId(rs.getInt("DIRECTOR_ID"));
            director.setName(rs.getString("DIRECTOR_NAME"));
            directorsByFilmId.computeIfAbsent(filmId, k -> new HashSet<>()).add(director);
        });

        for (Film film : films) {
            Set<Director> directors = directorsByFilmId.getOrDefault(film.getId(), new HashSet<>());
            film.setDirectors(directors);
        }
        return films;
    }

    private List<Film> filmsWithGenres(List<Film> films) {
        if (films.isEmpty()) {
            return films;
        }

        String sqlQueryGenre = "SELECT FG.FILM_ID, G.GENRE_ID, G.GENRE_NAME " +
                "FROM FILM_GENRE AS FG " +
                "INNER JOIN GENRE AS G ON FG.GENRE_ID = G.GENRE_ID ;";
        Map<Integer, Set<Genre>> genresByFilmId = new HashMap<>();

        jdbc.query(sqlQueryGenre, (rs) -> {
            int filmId = rs.getInt("FILM_ID");
            Genre genre = new Genre();
            genre.setId(rs.getInt("GENRE_ID"));
            genre.setName(rs.getString("GENRE_NAME"));
            genresByFilmId.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
        });

        for (Film film : films) {
            Set<Genre> genres = genresByFilmId.getOrDefault(film.getId(), new HashSet<>());
            film.setGenres(genres);
        }
        return films;
    }

    private void addGenres(Set<Genre> genres, int filmId) {
        if (genres != null) {
            for (Genre genre : genres) {
                jdbc.update("INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES(?, ?)", filmId, genre.getId());
            }
        }
    }

    private void addDirectors(Set<Director> directors, int filmId) {
        if (directors != null) {
            for (Director director : directors) {
                jdbc.update("INSERT INTO FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) VALUES(?, ?)", filmId,
                        director.getId());
            }
        }
    }
}