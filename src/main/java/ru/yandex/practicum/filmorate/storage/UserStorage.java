package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Component
public interface UserStorage {
    User createUser(User user);

    List<User> getAll();

    User updateUser(User newUser);

    Optional<User> getUserById(int id);
}