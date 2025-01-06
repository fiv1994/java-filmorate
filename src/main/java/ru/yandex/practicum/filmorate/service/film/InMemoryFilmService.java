package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GeneralException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Override
    public Film createFilm(Film film) {
        checkForRelatedData(film);
        return filmStorage.createFilm(film);
    }

    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film updateFilm(Film newFilm) {
        final Integer newFilmId = newFilm.getId();
        if (newFilmId == null) {
            throw new ValidationException("ID должен быть указан");
        }

        final Film existingFilm = filmStorage.getFilmById(newFilmId)
                .orElseThrow(() -> new GeneralException("Фильм с ID " + newFilmId + " не найден"));

        checkForRelatedData(newFilm);

        return filmStorage.updateFilm(newFilm);
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film existingFilm = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new GeneralException("Фильм с ID " + filmId + " не найден"));

        if (!userStorage.containsUserById(userId)) {
            throw new GeneralException("Пользователь c ID " + userId + " не найден");
        }
        if (!filmStorage.isLikeExist(filmId, userId)) {
            filmStorage.addLike(filmId, userId);
        } else {
            log.info("Лайк этим пользователем этому фильму уже был добавлен ранее");
        }
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film existingFilm = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new GeneralException("Фильм с id = " + filmId + " не найден"));

        if (!userStorage.containsUserById(userId)) {
            throw new GeneralException("Пользователь c id = " + filmId + " не найден");
        }
        if (filmStorage.isLikeExist(filmId, userId)) {
            filmStorage.removeLike(filmId, userId);
        } else {
            log.info("Лайк не найден");
        }
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    @Override
    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new GeneralException("Фильм с ID " + id + " не найден"));
    }

    private void checkForRelatedData(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            RatingMPA ratingMPA = mpaStorage.getMpaById(film.getMpa().getId());
            if (ratingMPA == null) {
                throw new ValidationException("MPA с id " + film.getMpa().getId() + " не найден");
            }
            film.getMpa().setName(ratingMPA.getName());
        }

        if (film.getGenres() != null) {
            final List<Integer> newFilmsGenreIds = film.getGenres().stream().map(Genre::getId).toList();
            final List<Genre> genresInDB = genreStorage.getByIds(newFilmsGenreIds);
            if (newFilmsGenreIds.size() != genresInDB.size()) {
                throw new ValidationException("Жанры не найдены");
            }
        }
    }
}