package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcReviewRepository {
    private final JdbcTemplate jdbc;

    private static Review createReview(ResultSet resultSet, int row) throws SQLException {
        Review review = new Review();
        review.setReviewId(resultSet.getInt("REVIEW_ID"));
        review.setContent(resultSet.getString("CONTENT"));
        review.setIsPositive(resultSet.getBoolean("IS_POSITIVE"));
        review.setUserId(resultSet.getInt("USER_ID"));
        review.setFilmId(resultSet.getInt("FILM_ID"));
        review.setUseful(resultSet.getInt("USEFUL"));
        return review;
    }

    public Review createReview(Review review) {

        String sqlQuery = "INSERT INTO REVIEWS (CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL) VALUES(?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, new String[]{"REVIEW_ID"});
            preparedStatement.setString(1, review.getContent());
            preparedStatement.setBoolean(2, review.getIsPositive());
            preparedStatement.setInt(3, review.getUserId());
            preparedStatement.setInt(4, review.getFilmId());
            preparedStatement.setInt(5, review.getUseful());
            return preparedStatement;
        }, keyHolder);

        Integer reviewId = keyHolder.getKeyAs(Integer.class);
        review.setReviewId(reviewId);

        if (reviewId == null) {
            throw new NotFoundException("Отзыв не добавлен");
        }
        return review;
    }

    public Review update(Review review) {

        int id = review.getReviewId();
        jdbc.update("UPDATE REVIEWS SET  CONTENT=?, IS_POSITIVE=?, USEFUL=? " +
                        "WHERE REVIEW_ID=?;",
                review.getContent(),
                review.getIsPositive(),
                review.getUseful(),
                id);
        return get(id);
    }

    public void deleteById(int id) {
        jdbc.update("DELETE FROM REVIEWS WHERE REVIEW_ID=?", id);
    }

    public Review get(int reviewId) {

        String sqlQuery = "SELECT REVIEW_ID," +
                " CONTENT," +
                " IS_POSITIVE," +
                " USER_ID," +
                " FILM_ID," +
                " USEFUL " +
                "FROM REVIEWS " +
                "WHERE REVIEW_ID = ?;";

        List<Review> reviews = jdbc.query(sqlQuery, JdbcReviewRepository::createReview, reviewId);
        if (reviews.size() != 1) {
            log.error("Отзыв с идентификатором {} не найден.", reviewId);
            throw new NotFoundException("Отзыв не найден id=" + reviewId);
        }
        Review review = reviews.getFirst();
        review.setUseful(calculateUseful(review));
        log.info("Найден отзыв -->" + review);
        return review;
    }

    public List<Review> getAll(int count) {
        String sqlQuery = "SELECT REVIEW_ID," +
                " CONTENT," +
                " IS_POSITIVE," +
                " USER_ID," +
                " FILM_ID," +
                " USEFUL" +
                " FROM REVIEWS " +
                "LIMIT ?;";

        List<Review> reviews = jdbc.query(sqlQuery, JdbcReviewRepository::createReview, count);

        return reviews.stream()
                .peek(review -> review.setUseful(calculateUseful(review)))
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    public List<Review> getAllByFilmId(int filmId, int count) {
        String sqlQuery = "SELECT REVIEW_ID," +
                " CONTENT," +
                " IS_POSITIVE," +
                " USER_ID," +
                " FILM_ID," +
                " USEFUL" +
                " FROM REVIEWS " +
                " WHERE FILM_ID=?" +
                " LIMIT ?;";

        List<Review> reviews = jdbc.query(sqlQuery, JdbcReviewRepository::createReview, filmId, count);

        return reviews.stream()
                .peek(review -> review.setUseful(calculateUseful(review)))
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    public void setUseful(int reviewId, int userId, int like) {
        String sqlQuery = "MERGE INTO GRADE_REVIEWS AS gr " +
                "USING (VALUES (?, ?, ?)) AS val (USER_ID, REVIEW_ID, IS_LIKE) " +
                "ON gr.REVIEW_ID = val.REVIEW_ID AND gr.USER_ID = val.USER_ID " +
                "WHEN MATCHED THEN " +
                "    UPDATE SET gr.IS_LIKE = val.IS_LIKE " +
                "WHEN NOT MATCHED THEN " +
                "    INSERT (USER_ID, REVIEW_ID, IS_LIKE) " +
                "    VALUES (val.USER_ID, val.REVIEW_ID, val.IS_LIKE);";

        jdbc.update(sqlQuery, userId, reviewId, like);
    }

    public void removeGrade(int reviewId, int userId) {
        jdbc.update("DELETE FROM GRADE_REVIEWS WHERE REVIEW_ID = ? AND USER_ID = ?",
                reviewId, userId);
    }

    private int calculateUseful(Review review) {
        String sqlQuery = "SELECT SUM(IS_LIKE)" +
                " FROM GRADE_REVIEWS " +
                " WHERE REVIEW_ID=?;";

        Integer sumIsLike = jdbc.queryForObject(sqlQuery, Integer.class, review.getReviewId());
        return sumIsLike != null ? sumIsLike : 0;
    }
}
