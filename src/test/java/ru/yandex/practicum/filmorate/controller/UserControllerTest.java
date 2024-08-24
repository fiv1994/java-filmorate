package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserStorage userStorage;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService, userStorage);
    }

    @Test
    void shouldFailOnEmptyEmail() {
        User user = new User();
        user.setEmail("");
        user.setLogin("Test");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> userController.createUser(user),
                "Ожидалось, что createUser() выдаст исключение, но этого не произошло."
        );
        assertTrue(thrown.getMessage().contains("Электронная почта должна быть задана"));
    }

    @Test
    void shouldFailOnSymbolEmail() {
        User user = new User();
        user.setEmail("mail.ru");
        user.setLogin("Test");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> userController.createUser(user),
                "Ожидалось, что createUser() выдаст исключение, но этого не произошло."
        );
        assertTrue(thrown.getMessage().contains("содержать символ '@'"));
    }

    @Test
    void shouldFailOnEmptyLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> userController.createUser(user),
                "Ожидалось, что createUser() выдаст исключение, но этого не произошло."
        );
        assertTrue(thrown.getMessage().contains("Логин не может быть пустым"));
    }

    @Test
    void shouldFailOnBackspaceLogin() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("Test User");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> userController.createUser(user),
                "Ожидалось, что createUser() выдаст исключение, но этого не произошло."
        );
        assertTrue(thrown.getMessage().contains("не должен содержать пробелы"));
    }

    @Test
    public void shouldTakeLoginToName() {
        User user = new User();
        user.setLogin("testuser");
        user.setEmail("test@example.com");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        userController.createUser(user);

        assertEquals("testuser", user.getName());
    }

    @Test
    void shouldFailOnBirthday() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("Test");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2500, 1, 1));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> userController.createUser(user),
                "Ожидалось, что createUser() выдаст исключение, но этого не произошло."
        );
        assertTrue(thrown.getMessage().contains("Дата рождения не может быть будущей"));
    }

    @Test
    void shouldCorrect() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("Test");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        assertDoesNotThrow(() -> userController.createUser(user),
                "Ожидалось, что createUser() не выдаст исключение, но оно произошло.");
    }
}