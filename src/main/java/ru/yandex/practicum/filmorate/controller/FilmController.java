package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;
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
    public ArrayList<Film> getAll() {
        ArrayList<Film> tempListFilms = filmService.getAll();
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
    public Film setLike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        Film tempFilm = filmService.setLike(id, userId);
        log.info("Set like film ==>" + tempFilm);
        return tempFilm;
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film deleterLike(@PathVariable @Positive int id, @PathVariable @Positive int userId) {
        Film film = filmService.deleteLike(id, userId);
        log.info("User id " + userId + " is remove like from film==>" + film);
        return film;
    }

    @GetMapping("popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        List<Film> films = filmService.getPopularFilms(count);
        log.info("Most popular films ==>" + films);
        return films;
    }
}
