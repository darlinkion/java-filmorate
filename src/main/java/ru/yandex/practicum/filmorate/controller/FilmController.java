package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends BaseController<Film> {

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        Film tempFilm = create(film);
        log.info("Add film ==>" + tempFilm);
        return tempFilm;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        Film tempFilm = update(film);
        log.info("Update film ==>" + tempFilm);
        return tempFilm;
    }

    @GetMapping
    public ArrayList<Film> getAllFilms() {
        ArrayList<Film> tempListFilms = getAll();
        log.info("Get all films from DataBase ==>" + tempListFilms);
        return tempListFilms;
    }

}
