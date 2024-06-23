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

import java.util.Map;

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
                .addValues()

        return null;
    }

}
