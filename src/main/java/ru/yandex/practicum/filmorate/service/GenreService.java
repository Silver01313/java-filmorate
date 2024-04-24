package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Slf4j
@Getter
@Service
public class GenreService {
    private final FilmStorage filmStorage;

    public GenreService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public List<Genre> getGenres() {
        return filmStorage.getGenres();
    }

    public Genre getGenreById(Integer id) {
        return filmStorage.getGenreById(id);
    }
}
