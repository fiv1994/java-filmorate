package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class InMemoryUserService implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public InMemoryUserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User createUser(User user) {
        log.info("Пришёл запрос на добавление пользователя с ником '{}'", user.getLogin());
        validate(user);
        User createdUser = userStorage.createUser(user);
        log.info("Пользователь с ником '{}' успешно добавлен.", user.getLogin());
        return createdUser;
    }

    @Override
    public List<User> getAll() {
        return userStorage.getAll();
    }

    @Override
    public User updateUser(User newUser) {
        log.info("Пришёл запрос на обновление данных пользователя с ником '{}'", newUser.getLogin());
        validate(newUser);
        User updatedUser = userStorage.updateUser(newUser);
        log.info("Обновлены данные пользователя с ID '{}'", newUser.getId());
        return updatedUser;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        log.info("Пользователю с ID '{}' пришёл запрос на добавление в друзья от пользователя с ID '{}'", userId,
                friendId);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь с ID '" + userId + " не найден, запрос на добавление" +
                    " в друзья не был обработан");
        } else if (friend == null) {
            throw new EntityNotFoundException("Пользователь с ID '" + friendId + " не найден, запрос на добавление" +
                    " в друзья от пользователя с ником " + user.getLogin() + " не был обработан");
        } else {
            log.info("Пользователю с ником '{}' пришёл запрос на добавление в друзья от пользователя с ником '{}'",
                    user.getLogin(), friend.getLogin());
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
            userStorage.updateUser(user);
            userStorage.updateUser(friend);
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь с ID '" + userId + "' не найден, удаление из друзей" +
                    "невозможно");
        } else if (friend == null) {
            throw new EntityNotFoundException("Пользователь с ID '" + friendId + "' не найден, удаление из друзей" +
                    "от пользователя '" + user.getLogin() + "' невозможно");
        } else {
            log.info("Пользователь '{}' хочет удалить из друзей '{}'", user.getLogin(), friend.getLogin());
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
            log.info("Пользователь '{}' удалил из друзей '{}'", user.getLogin(), friend.getLogin());
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь с ID '" + userId + "' не найден");
        }

        List<User> friends = new ArrayList<>();
        for (int friendId : user.getFriends()) {
            User friend = userStorage.getUserById(friendId);
            if (friend != null) {
                log.info("Найден друг пользователя '{}' - пользователь '{}'", user.getLogin(), friend.getLogin());
                friends.add(friend);
            } else {
                log.info("Не найден друг пользователя '{}' - пользователь '{}'", user.getLogin(), friend.getLogin());
            }
        }
        log.info("Получен список друзей пользователя '{}'", user.getLogin());
        return friends;
    }

    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        log.info("Запрос списка общих друзей для пользователей '{}' и '{}'", user.getLogin(), friend.getLogin());
        if (user != null && friend != null) {
            List<User> commonFriends = new ArrayList<>();
            for (int id : user.getFriends()) {
                if (friend.getFriends().contains(id)) {
                    commonFriends.add(userStorage.getUserById(id));
                }
            }
            log.info("Список общих друзей для пользователей '{}' и '{}' успешно получен. Количество общих друзей: {}",
                    user.getLogin(), friend.getLogin(), commonFriends.size());
            return commonFriends;
        } else {
            throw new EntityNotFoundException("Пользователи с ID " + userId + " или " + friendId + " не найдены");
        }
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта должна быть задана и содержать символ '@'.");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и не должен содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            log.info("Пользователь не задал имя, посему будет отображаться его логин.");
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть будущей.");
        }
    }
}
