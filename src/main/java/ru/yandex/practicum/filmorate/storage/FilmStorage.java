package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

@Component
public interface FilmStorage {
    Film createFilm(Film film);

    List<Film> getAll();

    Film updateFilm(Film newFilm);

    Optional<Film> getFilmById(int id);
}