package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GeneralException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InMemoryUserService implements UserService {
    private final UserStorage userStorage;

    @Override
    public User createUser(User user) {
        if (userStorage.existByEmail(user.getEmail())) {
            throw new ValidationException("Этот e-mail уже используется");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User updateUser(User newUser) {
        Integer newUserId = newUser.getId();
        if (newUserId == null) {
            throw new ValidationException("ID должен быть указан");
        }
        if (!userStorage.containsUserById(newUserId)) {
            throw new GeneralException("Service: Пользователь с ID " + newUserId + " не найден");
        }

        if (userStorage.existByEmail(newUser.getEmail())) {
            throw new ValidationException("Этот имейл уже используется");
        }
        return userStorage.updateUser(newUser);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (!userStorage.containsUserById(userId)) {
            throw new GeneralException("Пользователь c ID " + userId + " не найден");
        }
        if (!userStorage.containsUserById(friendId)) {
            throw new GeneralException("Пользователь c ID " + friendId + " не найден");
        }
        userStorage.addFriend(userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        if (!userStorage.containsUserById(userId)) {
            throw new GeneralException("Пользователь c ID " + userId + " не найден");
        }
        if (!userStorage.containsUserById(friendId)) {
            throw new GeneralException("Пользователь c ID " + friendId + " не найден");
        }
        userStorage.deleteFriend(userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        if (!userStorage.containsUserById(userId)) {
            throw new GeneralException("Пользователь c ID " + userId + " не найден");
        }
        return userStorage.getFriendsList(userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        if (!userStorage.containsUserById(userId)) {
            throw new GeneralException("Пользователь c ID " + userId + " не найден");
        }
        if (!userStorage.containsUserById(friendId)) {
            throw new GeneralException("Пользователь c ID " + friendId + " не найден");
        }
        return userStorage.getCommonFriendsList(userId, friendId);
    }
}
