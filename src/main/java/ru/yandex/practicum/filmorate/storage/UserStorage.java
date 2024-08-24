package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {
    User createUser(User user);
    List<User> getAll();
    User updateUser(User newUser);
    User getUserById(int id);
}