package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.GeneralException;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InMemoryMpaService implements MpaService {

    private final MpaStorage mpaStorage;

    @Override
    public List<RatingMPA> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    @Override
    public RatingMPA getMpaById(Integer id) {
        RatingMPA ratingMPA = mpaStorage.getMpaById(id);
        if (ratingMPA == null) {
            throw new GeneralException("Рейтинг MPA с ID " + id + " отсутствует");
        }
        return ratingMPA;
    }
}
