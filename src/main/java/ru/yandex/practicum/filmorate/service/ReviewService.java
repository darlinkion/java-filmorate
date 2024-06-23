package ru.yandex.practicum.filmorate.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.JdbcReviewRepository;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final JdbcReviewRepository jdbcReviewRepository;

    public Review create(Review temp) {
        return null;
    }


    public Review update(Review temp) {
        return null;
    }

    public void deleteById(int id){

    }

    public Review get(int id) {
        return null;
    }

    public List<Review> getAll(Integer filmId, Integer count) {
        return null;
    }

    public void setLike(int id, int userId){

    }

    public void setDislike(int id, int userId){

    }

    public void removeLike(int id, int userId){

    }

    public void removeDislike(int id, int userId){

    }
}
