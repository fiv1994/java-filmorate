package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int idGenerator = 1;

    @Override
    public User createUser(User user) {
        user.setId(idGenerator++);
        user.setFriends(new HashSet<>());
        user.setLikedFilms(new HashSet<>());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("ID пользователя должен быть указан.");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            log.info("Найден пользователь с ID '{}'", newUser.getId());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setBirthday(newUser.getBirthday());
            return oldUser;
        }
        throw new EntityNotFoundException("Пользователь с ID " + newUser.getId() + " не найден.");
    }

    @Override
    public Optional<User> getUserById(int id) {
        User user = users.get(id);
        log.info("Ищем пользователя с ID '{}'", users.get(id));
        if (user == null) {
            throw new EntityNotFoundException("Фильм с ID '" + id + "' не найден");
        }
        return Optional.of(user);
    }

}