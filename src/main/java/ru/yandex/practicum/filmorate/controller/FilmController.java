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
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        List<Film> films = filmService.getPopularFilms(count);
        log.info("Most popular films ==>" + films);
        return films;
    }

    @GetMapping("director/{directorId}?sortBy=year")
    public List<Film> getAllDirectorFilm(@PathVariable @Positive int directorId) {
        List<Film> films = filmService.getAllDirectorsFilms(directorId, "RELEASE_DATE");
        log.info("Most popular films ==>" + films);
        return films;
    }

    @GetMapping("director/{directorId}?sortBy=likes")
    public List<Film> getAllDirectorFilm(@PathVariable @Positive int directorId) {
        List<Film> films = filmService.getAllDirectorsFilms(directorId, "likes");
        log.info("Most popular films ==>" + films);
        return films;
    }
}
