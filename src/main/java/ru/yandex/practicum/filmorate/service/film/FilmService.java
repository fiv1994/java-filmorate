package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film createFilm(Film film);

    List<Film> getAll();

    Film updateFilm(Film newFilm);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getMostPopularFilms(int count);

    Film getFilmById(int id);
}