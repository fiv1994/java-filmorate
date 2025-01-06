package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.List;

public interface MpaStorage {

    List<RatingMPA> getAllMpa();

    RatingMPA getMpaById(int id);
}
