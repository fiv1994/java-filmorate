package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InMemoryGenreService implements GenreService {

    private final GenreStorage genreStorage;

    @Override
    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    @Override
    public Genre getGenreById(Integer id) {
        return genreStorage.getGenreById(id);
    }
}
