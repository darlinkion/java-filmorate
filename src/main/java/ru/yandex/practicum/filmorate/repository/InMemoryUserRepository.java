package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserRepository implements Repository<User> {

    private final HashMap<Integer, User> dataBase;
    private final HashMap<Integer, Set<User>> userFriendsDataBase;

    private Integer id = 0;

    @Override
    public User create(User film) {
        Integer tempId = generateId();
        film.setId(tempId);
        dataBase.put(tempId, film);
        return film;
    }

    @Override
    public User update(User temp) {
        Integer tempId = temp.getId();
        if (!dataBase.containsKey(tempId)) {
            throw new ValidationException("Обновление невозможно пробемы с id " + temp);
        }
        log.info("Id при запроси у объекта" + tempId);
        dataBase.remove(tempId);
        dataBase.put(tempId, temp);
        return temp;
    }

    @Override
    public ArrayList<User> getAll() {
        return new ArrayList<User>(dataBase.values());
    }

    @Override
    public User get(Integer userId) {
        return dataBase.get(userId);
    }

    @Override
    public boolean delete(int userId, int friendId) {
        Set<User> friendsSetUser = userFriendsDataBase.get(userId);
        Set<User> friendsSetUserFriend = userFriendsDataBase.get(friendId);

        boolean firstRemove = friendsSetUser.remove(dataBase.get(friendId));
        boolean secondRemove = friendsSetUserFriend.remove(dataBase.get(userId));

        userFriendsDataBase.put(userId, friendsSetUser);
        userFriendsDataBase.put(friendId, friendsSetUserFriend);

        return firstRemove && secondRemove;
    }

    public User addFriends(int userId, int friendId) {
        Set<User> friendsSetUser = userFriendsDataBase.get(userId);
        Set<User> friendsSetUserFriend = userFriendsDataBase.get(friendId);

        if (friendsSetUser == null) {
            friendsSetUser = new HashSet<>();
        }
        if (friendsSetUserFriend == null) {
            friendsSetUserFriend = new HashSet<>();
        }

        friendsSetUser.add(dataBase.get(friendId));
        friendsSetUserFriend.add(dataBase.get(userId));

        userFriendsDataBase.put(userId, friendsSetUser);
        userFriendsDataBase.put(friendId, friendsSetUserFriend);

        return dataBase.get(userId);
    }

    public boolean checkFriends(int userId, int friendId) {
        Set<User> friendsSetUser = userFriendsDataBase.get(userId);
        Set<User> friendsSetUserFriend = userFriendsDataBase.get(friendId);

        if (friendsSetUser == null || friendsSetUserFriend == null) {
            return false;
        }
        boolean checkUser = friendsSetUser.contains(dataBase.get(friendId));
        boolean checkFriends = friendsSetUserFriend.contains(dataBase.get(userId));

        return checkUser && checkFriends;
    }

    public ArrayList<User> getAllFriends(int id) {
        if (!userFriendsDataBase.containsKey(id)) {
            return new ArrayList<>();
        }
        return new ArrayList<User>(userFriendsDataBase.get(id));
    }

    public ArrayList<User> getAllMutualFriends(int userId, int friendId) {
        Set<User> friendsSetUser = userFriendsDataBase.get(userId);
        Set<User> friendsSetUserFriend = userFriendsDataBase.get(friendId);

        friendsSetUser.retainAll(friendsSetUserFriend);
        return new ArrayList<User>(friendsSetUser);
    }

    private Integer generateId() {
        if (id < Integer.MAX_VALUE) {
            return ++id;
        }
        return -1;
    }
}
