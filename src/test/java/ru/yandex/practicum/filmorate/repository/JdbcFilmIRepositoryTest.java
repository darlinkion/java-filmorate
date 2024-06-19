package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(JdbcFilmIRepository.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class JdbcFilmIRepositoryTest {

    private final JdbcFilmIRepository jdbcFilmIRepository;

    private static Film getFilm() {
        Film film = new Film();
        film.setName("pirog");
        film.setDescription("best pirog  ever");
        film.setReleaseDate(LocalDate.of(2000, 4, 11));
        film.setDuration(90);
        film.setMpa(new Mpa());
        film.getMpa().setId(1);
        Genre genre = new Genre();
        genre.setId(5);
        Set<Genre> genreSet = new HashSet<>();
        genreSet.add(genre);
        film.setGenres(genreSet);
        return film;
    }

    @Test
    void create() {
        Film film = new Film();
        film.setName("mama mia");
        film.setDescription("kfmdkfmdsmfs");
        film.setReleaseDate(LocalDate.of(2000, 4, 11));
        film.setDuration(100);
        film.setMpa(new Mpa());
        film.getMpa().setId(2);
        Genre genre = new Genre();
        genre.setId(2);
        Set<Genre> genreSet = new HashSet<>();
        genreSet.add(genre);
        film.setGenres(genreSet);

        Film film1 = jdbcFilmIRepository.create(film);
        film.setId(3);

        assertThat(film1)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(film);
    }

    @Test
    void get() {
        Film film1 = jdbcFilmIRepository.get(1);
        Film film = getFilm();

        assertThat(film1)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(film);
    }

    @Test
    void update() {
        Film film = getFilm();
        film.setName("One Piece");
        film.setId(1);

        Film testFilm = jdbcFilmIRepository.update(film);
        assertThat(film)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(testFilm);

    }

    @Test
    void getAll() {
        Film film1 = getFilm();
        film1.setId(1);
        film1.getMpa().setName("G");
        Genre genre1 = new Genre();
        genre1.setId(5);
        genre1.setName("Документальный");
        Set<Genre> genreSet1 = new LinkedHashSet<>();
        genreSet1.add(genre1);
        film1.setGenres(genreSet1);

        Film film2 = new Film();
        film2.setName("all dogs");
        film2.setDescription("best dogs  ever");
        film2.setReleaseDate(LocalDate.of(2019, 5, 17));
        film2.setDuration(129);
        film2.setMpa(new Mpa());
        film2.getMpa().setId(3);
        film2.getMpa().setName("PG-13");
        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        Set<Genre> genreSet = new LinkedHashSet<>();
        genreSet.add(genre);
        film2.setGenres(genreSet);
        film2.setId(2);

        List<Film> filmsBase = new ArrayList<>();
        filmsBase.add(film1);
        filmsBase.add(film2);

        List<Film> testFilms = jdbcFilmIRepository.getAll();

        assertThat(filmsBase)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(testFilms);
    }
}