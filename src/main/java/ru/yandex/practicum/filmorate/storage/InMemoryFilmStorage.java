package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idGenerator = 1;

    @Override
    public Film createFilm(Film film) {
        film.setId(idGenerator++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new ValidationException("ID фильма должен быть указан.");
        }
        log.info("Ищем фильм с ID '{}'", newFilm.getId());
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            log.info("Найден фильм с ID '{}'", newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Обновлены данные фильма с ID '{}'", newFilm.getId());
            return oldFilm;
        }
        throw new EntityNotFoundException("Фильм с ID " + newFilm.getId() + " не найден.");
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        log.info("Ищем фильм с ID '{}'", id);
        return Optional.ofNullable(films.get(id));
    }
}
