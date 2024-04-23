package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Integer addLike(Integer filmId, Integer userId) {
        try {
            userStorage.getUser(userId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        try {
            Set<Integer> likes = filmStorage.getFilm(filmId).getLikes();
            likes.add(userId);
            filmStorage.addLike(filmId, userId);
        } catch (NotFoundException e) {
            log.warn("Такого фильма не существует");
            throw new NotFoundException("Такого фильма не существует");
        }
        log.debug("Вы поставили лайк");
        return filmId;
    }

    public Integer removeLike(Integer filmId, Integer userId) {
        try {
            userStorage.getUser(userId);
        } catch (NotFoundException e) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        try {
            Set<Integer> likes = filmStorage.getFilm(filmId).getLikes();
            likes.remove(userId);
        } catch (NotFoundException e) {
            log.warn("Такого фильма не существует");
            throw new NotFoundException("Такого фильма не существует");
        }
        log.debug("Вы удалили лайк");
        return filmId;
    }

    public List<Film> getPopularFilmsList(Integer count) {
        log.debug("Список популярных фильмов");
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(film -> ((Film) film).getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film create(Film film) throws ValidationException, InvocationTargetException {
        return filmStorage.create(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film update(Film film) throws ValidationException {
        return filmStorage.update(film);
    }

    public Film getFilm(Integer id) {
        return filmStorage.getFilm(id);
    }

    public List<Genre> getGenres() {
        return filmStorage.getGenres();
    }

    public Genre getGenreById(Integer id) {
        return filmStorage.getGenreById(id);
    }

    public List<Mpa> getMpa() {
        return filmStorage.getMpa();
    }

    public Mpa getMpaById(Integer id) {
        return filmStorage.getMpaById(id);
    }
}
