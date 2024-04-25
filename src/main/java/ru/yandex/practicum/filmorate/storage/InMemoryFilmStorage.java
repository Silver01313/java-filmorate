package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private Integer filmId = 0;

    @Override
    public List<Film> findAll() {
        log.debug("Список всех фильмов");
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(@RequestBody Film film) throws ValidationException {
        if (film.getId() != null) {
            log.warn("Такой фильм уже существует");
            throw new ValidationException("Этот фильм уже зарегистрирован");
        }
        if (film.getName().isBlank()) {
            log.warn("Не корректное название");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Не корректное описание");
            throw new ValidationException("Описание должно быть не длиннее 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Не корректная дата выхода");
            throw new ValidationException("Дата выхода должна быть не ранее 28.12.1895г.");
        }
        if (film.getDuration() <= 0) {
            log.warn("Не корректная продолжительность");
            throw new ValidationException("Продолжительность фильма должна быть больше 0");
        }
        film = film.toBuilder().id(generateId()).build();
        films.put(film.getId(), film);
        log.debug("Вы добавили фильм : {}", film);
        return film;
    }

    @Override
    public Film update(@RequestBody Film film) throws ValidationException {
        if (!films.containsKey(film.getId())) {
            log.warn("Такого фильма не существует");
            throw new NotFoundException("Такого фильма не существует");
        }
        if (film.getName().isBlank()) {
            log.warn("Не корректное название");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Не корректное описание");
            throw new ValidationException("Описание должно быть не длиннее 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Не корректная дата выхода");
            throw new ValidationException("Дата выхода должна быть не ранее 28.12.1895г.");
        }
        if (film.getDuration() <= 0) {
            log.warn("Не корректная продолжительность");
            throw new ValidationException("Продолжительность фильма должна быть больше 0");
        }
        log.debug("Вы добавили фильм : {}", film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilm(Integer id) {
        if (!films.containsKey(id)) {
            log.warn("Такого фильма не существует");
            throw new NotFoundException("Такого фильма не существует");
        }
        return films.get(id);
    }

    @Override
    public Integer addLike(Integer filmId, Integer userId) {
        return null;
    }

    @Override
    public Integer removeLike(Integer filmId, Integer userId) {
        return null;
    }

    private Integer generateId() {
        return ++filmId;
    }
}
