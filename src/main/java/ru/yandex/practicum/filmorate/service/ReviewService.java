package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.type.EventType;
import ru.yandex.practicum.filmorate.model.type.OperationType;
import ru.yandex.practicum.filmorate.repository.JdbcEventRepository;
import ru.yandex.practicum.filmorate.repository.JdbcFilmIRepository;
import ru.yandex.practicum.filmorate.repository.JdbcReviewRepository;
import ru.yandex.practicum.filmorate.repository.JdbcUserRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final JdbcReviewRepository jdbcReviewRepository;
    private final JdbcUserRepository jdbcUserRepository;
    private final JdbcFilmIRepository jdbcFilmIRepository;
    private final JdbcEventRepository jdbcEventRepository;

    public Review create(Review temp) {
        jdbcUserRepository.get(temp.getUserId());
        jdbcFilmIRepository.get(temp.getFilmId());
        Review newReview = jdbcReviewRepository.createReview(temp);
        jdbcEventRepository.addEvent(new Event(Instant.now().toEpochMilli(), newReview.getUserId(),
                EventType.REVIEW, OperationType.ADD, newReview.getReviewId()));
        return newReview;
    }

    public Review update(Review temp) {
        //get(temp.getReviewId());
        Review updatedReview = jdbcReviewRepository.update(temp);
        jdbcEventRepository.addEvent(new Event(Instant.now().toEpochMilli(), updatedReview.getUserId(),
                EventType.REVIEW, OperationType.UPDATE, updatedReview.getReviewId()));
        return updatedReview;
    }

    public void deleteById(int id) {
        Review review = get(id);
        jdbcReviewRepository.deleteById(id);
        jdbcEventRepository.addEvent(new Event(Instant.now().toEpochMilli(), review.getUserId(),
                EventType.REVIEW, OperationType.REMOVE, review.getReviewId()));
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
        jdbcReviewRepository.setLike(reviewId, userId);
    }

    public void setDislike(int reviewId, int userId) {
        jdbcUserRepository.get(userId);
        get(reviewId);
        jdbcReviewRepository.setDislike(reviewId, userId);
    }

    public void removeGrade(int reviewId, int userId) {
        get(reviewId);
        jdbcUserRepository.get(userId);
        jdbcReviewRepository.removeGrade(reviewId, userId);
    }
}
