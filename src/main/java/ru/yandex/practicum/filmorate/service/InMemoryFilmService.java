package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Film createFilm(Film film) {
        log.info("Пришёл запрос на добавление фильма '{}'", film.getName());
        validate(film);
        Film createdFilm = filmStorage.createFilm(film);
        log.info("Фильм '{}' успешно добавлен.", film.getName());
        return createdFilm;
    }

    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film updateFilm(Film newFilm) {
        log.info("Пришёл запрос на обновление данных фильма '{}'", newFilm.getName());
        validate(newFilm);
        Film updatedFilm = filmStorage.updateFilm(newFilm);
        log.info("Обновлены данные пользователя с ID '{}'", newFilm.getId());
        return updatedFilm;
    }

    @Override
    public void addLike(int filmId, int userId) {
        Optional<Film> filmOptional = filmStorage.getFilmById(filmId);
        Optional<User> userOptional = userStorage.getUserById(userId);
        if (filmOptional.isPresent() && userOptional.isPresent()) {
            Film film = filmOptional.get();
            User user = userOptional.get();
            log.info("Пользователь '{}' хочет поставить лайк фильму '{}'", user.getLogin(), film.getName());
            film.getLikes().add(userId);
            user.getLikedFilms().add(filmId);
            filmStorage.updateFilm(film);
            userStorage.updateUser(user);
            log.info("От пользователя '{}' добавлен лайк фильму '{}'", user.getLogin(), film.getName());
        } else if (filmOptional.isEmpty()) {
            throw new EntityNotFoundException("Фильм с ID " + filmId + " не найден");
        } else if (userOptional.isEmpty()) {
            throw new ValidationException("Пользователь с ID " + userId + " не найден");
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Optional<Film> filmOptional = filmStorage.getFilmById(filmId);
        Optional<User> userOptional = userStorage.getUserById(userId);
        if (filmOptional.isPresent() && userOptional.isPresent()) {
            Film film = filmOptional.get();
            User user = userOptional.get();
            log.info("Пользователь '{}' хочет убрать лайк у фильма '{}'", user.getLogin(), film.getName());
            film.getLikes().remove(userId);
            user.getLikedFilms().remove(filmId);
            filmStorage.updateFilm(film);
            userStorage.updateUser(user);
            log.info("Пользователь '{}' убрал лайк от фильма '{}'", user.getLogin(), film.getName());
        } else if (filmOptional.isEmpty()) {
            throw new EntityNotFoundException("Фильм с ID " + filmId + " не найден");
        } else if (userOptional.isEmpty()) {
            throw new ValidationException("Пользователь с ID " + userId + " не найден");
        }
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        List<Film> films = filmStorage.getAll();
        films.sort((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()));
        return films.subList(0, Math.min(count, films.size()));
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        return Optional.ofNullable(filmStorage.getFilmById(id).orElseThrow(() ->
                new EntityNotFoundException("Фильм с ID '" + id + "' не найден")
        ));
    }

    private void validate(Film film) {
        LocalDate minDate = LocalDate.of(1895, 12, 28);
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("Должно быть задано название фильма.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может быть длиннее 200 символов.");
        }
        if (film.getReleaseDate().isBefore(minDate)) {
            throw new ValidationException("Фильм не может быть старше 28 декабря 1895 года.");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Длительность фильма может быть только положительной.");
        }
    }

}