package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcLikesRepository {
    private final JdbcTemplate jdbc;

    public void setLike(int filmId, int userId) {
        jdbc.update("INSERT INTO LIKES (FILM_ID,USER_ID) VALUES(?, ?);", filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        jdbc.update("DELETE FROM LIKES WHERE USER_ID=? AND FILM_ID=? ;", userId, filmId);
    }
}

