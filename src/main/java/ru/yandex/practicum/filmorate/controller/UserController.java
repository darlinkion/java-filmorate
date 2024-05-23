package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends BaseController<User> {

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        checkName(user);
        User tempUser = create(user);
        log.info("Add user ==>" + tempUser);
        return tempUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        checkName(user);
        User tempUser = update(user);
        log.info("Update user ==>" + tempUser);
        return tempUser;
    }

    @GetMapping
    public ArrayList<User> getAllUsers() {
        ArrayList<User> tempListUsers = getAll();
        log.info("Get all users from DataBase ==>" + tempListUsers);
        return tempListUsers;
    }

    private void checkName(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
