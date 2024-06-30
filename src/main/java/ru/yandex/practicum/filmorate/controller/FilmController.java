package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film create(@Valid @RequestBody Film temp) {
        Film tempFilm = filmService.create(temp);
        log.info("Add film ==>" + tempFilm);
        return tempFilm;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film temp) {
        Film tempFilm = filmService.update(temp);
        log.info("Update film ==>" + tempFilm);
        return tempFilm;
    }

    @GetMapping
    public List<Film> getAll() {
        List<Film> tempListFilms = filmService.getAll();
        log.info("Get all films from DataBase ==>" + tempListFilms);
        return tempListFilms;
    }

    @GetMapping("{id}")
    public Film get(@PathVariable @Positive int id) {
        Film tempFilm = filmService.get(id);
        log.info("Get film from DataBase  ==>" + tempFilm);
        return tempFilm;
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable @Positive int id) {
        filmService.deleteById(id);
        log.info("Delete film from DataBase with id ==>" + id);
    }

    @PutMapping("{id}/like/{userId}")
    public void setLike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        filmService.setLike(id, userId);
        log.info("Set like film id ==>" + id);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleterLike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        filmService.deleteLike(id, userId);
        log.info("User id " + userId + " is remove like from film id==>" + id);
    }

    @GetMapping("popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(required = false) Integer genreId,
            @RequestParam(required = false) Integer year) {
        List<Film> films = filmService.getPopularFilms(count, genreId, year);
        log.info("Most popular films ==>" + films);
        return films;
    }

    @GetMapping("/common")
    public List<Film> getMutualFilms(@RequestParam @Positive int userId, @RequestParam @Positive int friendId) {
        List<Film> tempListMutualFilms = filmService.getMutualFilms(userId, friendId);
        log.info("Friend films mutual from DataBase ==>" + tempListMutualFilms);
        return tempListMutualFilms;
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getAllDirectorFilms(
            @PathVariable @Positive int directorId,
            @RequestParam(required = false, defaultValue = "likes") String sortBy) {

        List<Film> films = filmService.getAllDirectorsFilms(directorId, sortBy);
        log.info("Most popular films ==> " + films);
        return films;
    }
}