package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcReviewRepository {
    private final JdbcTemplate jdbc;

    public Review createReview(Review review) {

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params= new MapSqlParameterSource()
                .addValues(Map.of("content", review.getContent()))
                .addValues(Map.of("isPositive", review.getIsPositive()))
                .addValues(Map.of("userId",review.getUserId()))
                .addValues(Map.of("filmId", review.getFilmId()))
                .addValues(Map.of("useful", review.getUseful()));

        jdbc.update("INSERT INTO REVIEW VALUES (CONTTENT,IS_POSITIVE," +
                "USER_ID,FILM_ID,USEFUL)" +
                " VALUES (:content,:isPositive,:userId,:filmId,:useful)"
                ,params, keyHolder, new String[]{"REVIEW_ID"});

        int reviewId = keyHolder.getKeyAs(int.class );
        review.setReviewId(reviewId);
        return review;
    }

    public Review update(Review review){
        return null;
    }
    public void deleteById(int id){

    }
    public Optional<Review> getReviewById(int reviewId) {
        SqlParameterSource params= new MapSqlParameterSource()
                .addValues(Map.of("reviewId",reviewId));


        return null;
    }

    public List<Review> getAll(){
        return null;
    }

    public void setLike(int reviewId, int userId){

    }

    public void setDislike(int reviewId, int userId){

    }

    public void removeLike(int reviewId, int userId){

    }

    public void removeDislike(int reviewId,int userId){

    }

    private static Review createReview(ResultSet resultSet, int row) throws SQLException {
        Review review=new Review();
        review.setReviewId(resultSet.getInt("REVIEW_ID"));
        review.setContent(resultSet.getString("CONTENT"));
        review.setIsPositive(resultSet.getBoolean("IS_POSITIVE"));
        review.setUserId(resultSet.getInt("USER_ID"));
        review.setFilmId(resultSet.getInt("FILM_ID"));
        review.setUseful(resultSet.getInt("USERFUL"));
        return review;
    }


}
