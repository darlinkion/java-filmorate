package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    // Получение списка всех режисеров +
    @GetMapping
    public List<Director> getAll() {
        List<Director> tempListDirectors = directorService.getAllDirectors();
        log.info("Get all directors from DataBase ==>" + tempListDirectors);
        return tempListDirectors;
    }

    // Получение режисера по id +
    @GetMapping("{id}")
    public Director getDirectorById(@PathVariable @Positive int id) {
        Director tempDirector = directorService.get(id);
        log.info("Get director from DataBase  ==>" + tempDirector);
        return tempDirector;
    }

    // Создание режиссера +
    @PostMapping
    public Director create(@Valid @RequestBody Director temp) {
        Director tempDirector = directorService.create(temp);
        log.info("Add director ==>" + tempDirector);
        return tempDirector;
    }

    // Обновление режиссера +
    @PutMapping
    public Director update(@Valid @RequestBody Director temp) {
        Director tempDirector = directorService.update(temp);
        log.info("Update director ==>" + tempDirector);
        return tempDirector;
    }

    // Удаление режиссера +
    @DeleteMapping("directors/{id}")
    public void deleteDirector(@PathVariable @Positive int id) {
        directorService.deleteDirector(id);
        log.info("Director with ID = " + id + " delited");
    }
}
