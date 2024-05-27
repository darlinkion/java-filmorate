package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.InMemoryUserRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements BaseService<User> {
    private final InMemoryUserRepository inMemoryUserRepository;

    @Override
    public User create(User user) {
        checkName(user);
        return inMemoryUserRepository.create(user);
    }

    @Override
    public User update(User user) {
        checkName(user);
        return inMemoryUserRepository.update(user);
    }

    @Override
    public ArrayList<User> getAll() {
        return inMemoryUserRepository.getAll();
    }

    @Override
    public User get(int id) {
        Optional<User> user = Optional.ofNullable(inMemoryUserRepository.get(id));
        if (!user.isPresent()) {
            throw new ValidationException("Нет такого пользователя по id " + id);
        }
        return user.get();
    }

    public User addFriends(int userId, int friendId) {

        Optional<User> friend = Optional.ofNullable(inMemoryUserRepository.get(friendId));
        if (!friend.isPresent()) {
            throw new ValidationException("Нет такого друга по id " + friendId);
        }
        Optional<User> user = Optional.ofNullable(inMemoryUserRepository.addFriends(userId, friendId));
        if (!user.isPresent()) {
            throw new ValidationException("Нет такого пользователя по id " + userId);
        }
        return user.get();
    }

    public boolean deleteFriends(int userId, int friendId) {
        checkUser(userId);
        checkUser(friendId);
        boolean isTheyFriends = inMemoryUserRepository.checkFriends(userId, friendId);
        if (!isTheyFriends) return true;
        return inMemoryUserRepository.delete(userId, friendId);
    }

    public ArrayList<User> getAllFriends(int id) {
        checkUser(id);
        ArrayList<User> temp = inMemoryUserRepository.getAllFriends(id);
        return temp;
    }

    public ArrayList<User> getAllMutualFriends(int userId, int friendId) {
        return inMemoryUserRepository.getAllMutualFriends(userId, friendId);
    }

    public void checkUser(int id) {
        if (inMemoryUserRepository.get(id) == null) {
            throw new ValidationException("Нет такого User id=>" + id);
        }
    }

    private void checkName(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
