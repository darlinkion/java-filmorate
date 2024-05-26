package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    UserService userService;

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
    public ArrayList<User> getAll() {
        ArrayList<User> tempListUsers = userService.getAll();
        log.info("Get all users from DataBase ==>" + tempListUsers);
        return tempListUsers;
    }

    @GetMapping("{id}")
    public User get(@PathVariable @Positive int id) {
        User tempUser = userService.get(id);
        log.info("Get user from DataBase  ==>" + tempUser);
        return tempUser;
    }

    @PutMapping("{id}/friends/{friendId}")
    public User addFriends(@PathVariable @Positive int id, @PathVariable @Positive int friendId) {
        User user = userService.addFriends(id, friendId);
        log.info(" friend added to user ==>" + user);
        return user;
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public boolean deleteFriend(@PathVariable @Positive int id, @PathVariable @Positive int friendId) {
        boolean result = userService.deleteFriends(id, friendId);
        log.info("User id " + id + " is remove  from friends user " + friendId + " result==>" + result);
        return result;
    }

    @GetMapping("{id}/friends")
    public ArrayList<User> getAllFriends(@PathVariable @Positive int id) {
        ArrayList<User> tempListUserFriends = userService.getAllFriends(id);
        log.info("Get all user friends from DataBase ==>" + tempListUserFriends);
        return tempListUserFriends;
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public ArrayList<User> getMutualFriends(@PathVariable @Positive int id, @PathVariable @Positive int otherId) {
        ArrayList<User> tempListMutualFriends = userService.getAllMutualFriends(id, otherId);
        log.info("Get all user mutual friends from DataBase ==>" + tempListMutualFriends);
        return tempListMutualFriends;
    }
}
