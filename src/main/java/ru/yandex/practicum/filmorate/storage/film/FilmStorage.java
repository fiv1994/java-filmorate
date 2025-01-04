package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film createFilm(Film film);

    List<Film> getAll();

    Film updateFilm(Film newFilm);

    Optional<Film> getFilmById(int id);

    List<Film> getPopularFilms(int count);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    boolean isLikeExist(int filmId, int userId);
}