package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.JdbcUserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements BaseService<User> {
    private final JdbcUserRepository jdbcUserRepository;

    @Override
    public User create(User user) {
        checkName(user);
        return jdbcUserRepository.create(user);
    }

    @Override
    public User update(User user) {
        checkName(user);
        return jdbcUserRepository.update(user);
    }

    @Override
    public List<User> getAll() {
        return jdbcUserRepository.getAll();
    }

    @Override
    public User get(int id) {
        Optional<User> user = Optional.ofNullable(jdbcUserRepository.get(id));
        if (user.isEmpty()) {
            throw new NotFoundException("Нет такого пользователя по id " + id);
        }
        return user.get();
    }

    public void addFriends(int userId, int friendId) {
        get(userId);
        get(friendId);
        jdbcUserRepository.addFriend(userId, friendId);
    }

    public void deleteFriends(int userId, int friendId) {
        get(userId);
        get(friendId);
        jdbcUserRepository.deleteFriends(userId, friendId);
    }

    public List<User> getAllFriends(int id) {
        get(id);
        return jdbcUserRepository.getAllFrineds(id);
    }

    public List<User> getAllMutualFriends(int userId, int friendId) {
        return jdbcUserRepository.getAllMutualFriends(userId, friendId);
    }

    private void checkName(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
