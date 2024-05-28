package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.InMemoryUserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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
        if (user.isEmpty()) {
            throw new NotFoundException("Нет такого пользователя по id " + id);
        }
        return user.get();
    }


    public User addFriends(int userId, int friendId) {

        User friend = get(friendId);
        User user = get(userId);

        friend.getFriends().add(userId);
        user.getFriends().add(friendId);

        return user;
    }

    public User deleteFriends(int userId, int friendId) {
        User friend = get(friendId);
        User user = get(userId);

        friend.getFriends().remove(userId);
        user.getFriends().remove(friendId);

        return friend;
    }

    public List<User> getAllFriends(int id) {
        return get(id)
                .getFriends()
                .stream()
                .map(friend -> get(friend))
                .collect(Collectors.toList());
    }

    public List<User> getAllMutualFriends(int userId, int friendId) {
        List<User> allFriendsUser1 = getAllFriends(userId);
        List<User> allFriendsUser2 = getAllFriends(friendId);

        allFriendsUser1.retainAll(allFriendsUser2);
        return allFriendsUser1;
    }

    private void checkName(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
    }
}
