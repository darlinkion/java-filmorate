package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        Review tempReview = reviewService.create(review);
        log.info("Add review ==>" + tempReview);
        return tempReview;
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        Review tempReview = reviewService.update(review);
        log.info("Update review ==>" + tempReview);
        return tempReview;
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable int id) {
        reviewService.deleteById(id);
        log.info("Delete review ==>" + id);
    }

    @GetMapping("{id}")
    public Review get(@PathVariable @Positive int id) {
        Review tempReview = reviewService.get(id);
        log.info("Get review from DataBase  ==>" + tempReview);
        return tempReview;
    }

    @GetMapping
    public List<Review> getReviews(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(defaultValue = "10") Integer count) {
        List<Review> reviews = reviewService.getAll(filmId, count);
        log.info("Get all reviews ==>" + reviews);
        return reviews;
    }

    @PutMapping("{id}/like/{userId}")
    public void setLike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        reviewService.setLike(id, userId);
        log.info("Set like review id ==>" + id);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void setDislike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        reviewService.setDislike(id, userId);
        log.info("Set dislike review id ==>" + id);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        reviewService.removeGrade(id, userId);
        log.info("Delete like review id ==>" + id);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public void removeDislike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        reviewService.removeGrade(id, userId);
        log.info("Delete dislike review id ==>" + id);
    }
}
