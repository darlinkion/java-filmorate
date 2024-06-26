package ru.yandex.practicum.filmorate.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.JdbcFilmIRepository;
import ru.yandex.practicum.filmorate.repository.JdbcReviewRepository;
import ru.yandex.practicum.filmorate.repository.JdbcUserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final JdbcReviewRepository jdbcReviewRepository;
    private final JdbcUserRepository jdbcUserRepository;

    public Review create(Review temp) {
        return jdbcReviewRepository.createReview(temp);
    }

    public Review update(Review temp) {
        get(temp.getReviewId());
        return jdbcReviewRepository.update(temp);
    }

    public void deleteById(int id) {
        get(id);
        jdbcReviewRepository.deleteById(id);
    }

    public Review get(int id) {
        return jdbcReviewRepository.get(id);
    }

    public List<Review> getAll(Integer filmId, Integer count) {
        if (filmId == null) {
            return jdbcReviewRepository.getAll(count);
        } else {
            return jdbcReviewRepository.getAllByFilmId(filmId, count);
        }
    }

    public void setLike(int reviewId, int userId) {
        jdbcUserRepository.get(userId);
        get(reviewId);
        jdbcReviewRepository.setLike(reviewId,userId);
    }

    public void setDislike(int reviewId, int userId) {

    }

    public void removeLike(int reviewId, int userId) {

    }

    public void removeDislike(int reviewId, int userId) {

    }
}
