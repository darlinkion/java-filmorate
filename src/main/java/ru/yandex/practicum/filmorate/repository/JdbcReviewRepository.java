package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcReviewRepository {
    private final JdbcTemplate jdbc;

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

    public Review update(Review review){

        int useful=calculateUseful(review);
        review.setUseful(useful);

        SqlParameterSource params= new MapSqlParameterSource()
                .addValues(Map.of("content", review.getContent()))
                .addValues(Map.of("isPositive", review.getIsPositive()))
                .addValues(Map.of("userId",review.getUserId()))
                .addValues(Map.of("filmId", review.getFilmId()))
                .addValues(Map.of("useful", review.getUseful()))
                .addValues((Map.of("reviewId",review.getReviewId())));

        jdbc.update( "UPDATE REVIEWS SET CONTENT = :content, IS_POSITIVE = :isPositive, " +
                "USER_ID = :userId, FILM_ID = :filmId, USEFUL = :useful " +
                "WHERE REVIEW_ID=:reviewId",params);

        return review;
    }
    public void deleteById(int id){
        jdbc.update("DELETE FROM REVIEWS WHERE REVIEW_ID=?",id);
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

        List<Review> reviews=jdbc.query(sqlQuery, JdbcReviewRepository::createReview,reviewId);
        if(reviews.size()!=1){
            log.error("Отзыв с идентификатором {} не найден.", reviewId);
            throw new NotFoundException("Отзыв не найден id="+reviewId);
        }
        Review review=reviews.getFirst();
        review.setUseful(calculateUseful(review));
        log.info("Найден отзыв -->"+review);
        return review;
    }

    public List<Review> getAll(int count){
        String sqlQuery = "SELECT REVIEW_ID," +
                " CONTENT," +
                " IS_POSITIVE," +
                " USER_ID," +
                " FILM_ID," +
                " USEFUL" +
                " FROM REVIEWS " +
                "LIMIT ?;";
        List<Review> reviews=jdbc.query(sqlQuery, JdbcReviewRepository::createReview,count);

        return reviews.stream()
                .peek(review -> review.setUseful(calculateUseful(review)))
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }
    public List<Review> getAllByFilmId(int filmId, int count){
        String sqlQuery = "SELECT REVIEW_ID," +
                " CONTENT," +
                " IS_POSITIVE," +
                " USER_ID," +
                " FILM_ID," +
                " USEFUL" +
                " FROM REVIEWS " +
                " WHERE FILM_ID=?" +
                " LIMIT ?;";
        List<Review> reviews=jdbc.query(sqlQuery, JdbcReviewRepository::createReview,filmId,count);

        return reviews.stream()
                .peek(review -> review.setUseful(calculateUseful(review)))
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    public void setLike(int reviewId, int userId){
        SqlParameterSource params= new MapSqlParameterSource()
                .addValues(Map.of("reviewId", reviewId))
                .addValues(Map.of("userId", userId))
                .addValues(Map.of("isLike", 1));

        jdbc.update("INSERT INTO GRADE_REVIEWS VALUES (USER_ID,REVIEW_ID," +
                        "IS_LIKE) VALUES (:reviewId,:userId,:isLike)",params);
    }

    public void setDislike(int reviewId, int userId){
        SqlParameterSource params= new MapSqlParameterSource()
                .addValues(Map.of("reviewId", reviewId))
                .addValues(Map.of("userId", userId))
                .addValues(Map.of("isLike", -1));

        jdbc.update("INSERT INTO GRADE_REVIEWS VALUES (USER_ID,REVIEW_ID," +
                "IS_LIKE) VALUES (:reviewId,:userId,:isLike)",params);
    }

    public void removeGrade(int reviewId, int userId){
        jdbc.update("DELETE FROM GRADE_REVIEWS WHERE REVIEW_ID=? AND USER_ID=?",reviewId,userId);
    }

    private static Review createReview(ResultSet resultSet, int row) throws SQLException {
        Review review=new Review();
        review.setReviewId(resultSet.getInt("REVIEW_ID"));
        review.setContent(resultSet.getString("CONTENT"));
        review.setIsPositive(resultSet.getBoolean("IS_POSITIVE"));
        review.setUserId(resultSet.getInt("USER_ID"));
        review.setFilmId(resultSet.getInt("FILM_ID"));
        review.setUseful(resultSet.getInt("USEFUL"));
        return review;
    }

    private int calculateUseful(Review review){
        String sqlQuery = "SELECT SUM(IS_LIKE)" +
                " FROM GRADE_REVIEWS " +
                " WHERE REVIEW_ID=?;";

        Integer sumIsLike = jdbc.queryForObject(sqlQuery, Integer.class, review.getReviewId());
        return sumIsLike != null ? sumIsLike : 0;
    }

}
