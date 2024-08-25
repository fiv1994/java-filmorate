package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

@Service
public interface FilmService {
    Film createFilm(Film film);

    List<Film> getAll();

    Film updateFilm(Film newFilm);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<Film> getMostPopularFilms(int count);

    Optional<Film> getFilmById(int id);
}