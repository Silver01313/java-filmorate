package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Slf4j
@Getter
@Service
public class MpaService {

    private final FilmStorage filmStorage;

    public MpaService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Mpa> getMpa() {
        return filmStorage.getMpa();
    }

    public Mpa getMpaById(Integer id) {
        return filmStorage.getMpaById(id);
    }
}
