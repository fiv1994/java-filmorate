package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    List<User> getAll();

    User updateUser(User newUser);

    boolean containsUserById(int id);

    boolean existByEmail(String email);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getFriendsList(int id);

    List<User> getCommonFriendsList(int id, int otherId);
}