package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public User create(@Valid @RequestBody User temp) {
        User tempUser = userService.create(temp);
        log.info("Add user ==>" + tempUser);
        return tempUser;
    }

    @PutMapping
    public User update(@Valid @RequestBody User temp) {
        User tempUser = userService.update(temp);
        log.info("Update user ==>" + tempUser);
        return tempUser;
    }

    @GetMapping
    public List<User> getAll() {
        List<User> tempListUsers = userService.getAll();
        log.info("Get all users from DataBase ==>" + tempListUsers);
        return tempListUsers;
    }

    @GetMapping("{id}")
    public User get(@PathVariable @Positive int id) {
        User tempUser = userService.get(id);
        log.info("Get user from DataBase  ==>" + tempUser);
        return tempUser;
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable @Positive int id) {
        userService.deleteById(id);
        log.info("Delete user from DataBase with id ==>" + id);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriends(@PathVariable @Positive int id, @PathVariable @Positive int friendId) {
        userService.addFriends(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable @Positive int id, @PathVariable @Positive int friendId) {
        userService.deleteFriends(id, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getAllFriends(@PathVariable @Positive int id) {
        List<User> tempListUserFriends = userService.getAllFriends(id);
        log.info("Get all user friends from DataBase ==>" + tempListUserFriends);
        return tempListUserFriends;
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable @Positive int id, @PathVariable @Positive int otherId) {
        List<User> tempListMutualFriends = userService.getAllMutualFriends(id, otherId);
        log.info("Get all user mutual friends from DataBase ==>" + tempListMutualFriends);
        return tempListMutualFriends;
    }

    @GetMapping("/{userId}/recommendations")
    public List<Film> getRecommendationFilms(@PathVariable @Positive long userId) {
        List<Film> recommendationFilms = userService.getRecommendationFilms(userId);
        log.info("For user with userId = " + userId + " Get recommendation films ==> " + recommendationFilms);
        return recommendationFilms;
    }

    @GetMapping("/{id}/feed")
    public List<Event> getUserFeed(@PathVariable @Positive int id) {
        List<Event> userFeed = userService.getUserEvents(id);
        log.info("For user user with ID = " + id + " Get feed ==> " + userFeed);
        return userFeed;
    }
}
