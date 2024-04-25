package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Component
public interface FilmStorage {
    Film create(Film film) throws ValidationException, InvocationTargetException;

    Film update(Film film) throws ValidationException;

    List<Film> findAll();

    Film getFilm(Integer id);

    public Integer addLike(Integer filmId, Integer userId);

    public Integer removeLike(Integer filmId, Integer userId);
}