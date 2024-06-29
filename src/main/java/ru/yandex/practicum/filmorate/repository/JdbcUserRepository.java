package ru.yandex.practicum.filmorate.repository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcUserRepository implements IRepository<User> {

    private final JdbcTemplate jdbc;

    private static User createUser(ResultSet resultSet, int row) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("ID"));
        user.setEmail(resultSet.getString("EMAIL"));
        user.setLogin(resultSet.getString("LOGIN"));
        user.setName(resultSet.getString("NAME"));
        user.setBirthday(resultSet.getDate("BIRTHDAY").toLocalDate());
        return user;
    }

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
    public User create(User user) {
        String sqlQuery = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES(?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"ID"});
            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setString(3, user.getName());
            preparedStatement.setDate(4, Date.valueOf(user.getBirthday()));
            return preparedStatement;
        }, keyHolder);

        user.setId(keyHolder.getKeyAs(Integer.class));
        if (user.getId() == null) {
            throw new NotFoundException("Пользователь не добавлен");
        }
        log.info("Пользователь с идентификатором {} добавлен.", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        get(user.getId());
        jdbc.update("UPDATE USERS SET EMAIL=?, LOGIN=?, NAME=?, BIRTHDAY=? WHERE ID=?;",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public List<User> getAll() {
        return jdbc.query("SELECT * FROM USERS", JdbcUserRepository::createUser);
    }

    @Override
    public User get(int id) {
        List<User> users = jdbc.query("SELECT * FROM USERS WHERE ID=?;",
                JdbcUserRepository::createUser, id);
        if (users.size() != 1) {
            throw new NotFoundException("Пользователь с идентификатором id=" + id + " не найден.");
        }
        User user = users.getFirst();
        log.info("Найден пользователь: {}", user);
        return user;
    }

    @Override
    public void deleteById(int id) {
        try {
            String sql = "delete from users where id = ?";
            jdbc.update(sql, id);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя из БД: {}", e.getMessage(), e);
            throw new NotFoundException("Ошибка при удалении пользователя из БД:");
        }
    }

    public void addFriend(int userId, int friendId) {
        jdbc.update("INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?,?)", userId, friendId);
    }

    public void deleteFriends(int userId, int friendId) {
        jdbc.update("DELETE FROM FRIENDS WHERE USER_ID=? and FRIEND_ID =?", userId, friendId);
    }

    public List<User> getAllFrineds(int id) {
        return jdbc.query("SELECT * FROM USERS" +
                        " WHERE ID in (SELECT F.FRIEND_ID" +
                        " FROM FRIENDS AS F" +
                        " WHERE F.USER_ID=?)",
                JdbcUserRepository::createUser, id);
    }

    public List<User> getAllMutualFriends(int userId, int friendId) {
        return jdbc.query("SELECT U.*" +
                        " FROM USERS AS U " +
                        "INNER JOIN FRIENDS f1 ON u.ID = f1.FRIEND_ID AND f1.USER_ID =?" +
                        "INNER JOIN FRIENDS f2 ON u.ID = f2.FRIEND_ID AND f2.USER_ID=?;",
                JdbcUserRepository::createUser, friendId, userId);
    }

    public List<Film> getRecommendationFilms(long userId) {
        String sql = "SELECT * FROM FILM AS f " +
                "JOIN RATING AS r ON f.RATING_ID = r.RATING_ID " +
                "WHERE f.FILM_ID IN ( " +
                "SELECT FILM_ID FROM LIKES " +
                "WHERE USER_ID IN ( " +
                "SELECT l1.USER_ID FROM LIKES AS l1 " +
                "RIGHT JOIN LIKES AS l2 ON l2.FILM_ID = l1.FILM_ID " +
                "GROUP BY l1.USER_ID, l2.USER_ID " +
                "HAVING l1.USER_ID IS NOT NULL " +
                "AND l1.USER_ID != ? AND l2.USER_ID = ? " +
                "ORDER BY COUNT(l1.USER_ID) DESC " +
                ") " +
                "AND f.FILM_ID NOT IN (  " +
                "SELECT FILM_ID FROM LIKES " +
                "WHERE USER_ID = ? " +
                ") " +
                ");";
        return jdbc.query(sql, JdbcUserRepository::createFilm, userId, userId, userId);
    }
}