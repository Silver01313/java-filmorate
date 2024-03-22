package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class FilmValidationTests {

    private Film film;
    private FilmController filmController;

    @BeforeEach
    public void createFilmAndController() {
        film = Film.builder()
                .name("a")
                .description("b")
                .releaseDate(LocalDate.of(2024, 03, 18))
                .duration(90)
                .build();
        filmController = new FilmController();
    }

    @Test
    public void shouldTrowExceptionIfNameIsEmpty() throws ValidationException {
        Film film1 = film.toBuilder().name(" ").build();

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(film1));
        assertEquals("Название не может быть пустым", exception.getMessage());

        film = filmController.create(film);
        Film film2 = film.toBuilder().name(" ").build();

        ValidationException exception2 = assertThrows(ValidationException.class, () -> filmController.update(film2));
        assertEquals("Название не может быть пустым", exception2.getMessage());
    }

    @Test
    public void shouldTrowExceptionIfDescriptionTooLong() throws ValidationException {
        String longDescription = "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss" +
                "sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss";
        Film film1 = film.toBuilder().description(longDescription).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(film1));
        assertEquals("Описание должно быть не длиннее 200 символов", exception.getMessage());

        film = filmController.create(film);
        Film film2 = film.toBuilder().description(longDescription).build();

        ValidationException exception2 = assertThrows(ValidationException.class, () -> filmController.update(film2));
        assertEquals("Описание должно быть не длиннее 200 символов", exception2.getMessage());
    }

    @Test
    public void shouldTrowExceptionIfDateOfReleaseTooEarly() throws ValidationException {
        Film film1 = film.toBuilder().releaseDate(LocalDate.of(1895, 12, 27)).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(film1));
        assertEquals("Дата выхода должна быть не ранее 28.12.1895г.", exception.getMessage());

        film = filmController.create(film);
        Film film2 = film.toBuilder().releaseDate(LocalDate.of(1895, 12, 27)).build();

        ValidationException exception2 = assertThrows(ValidationException.class, () -> filmController.update(film2));
        assertEquals("Дата выхода должна быть не ранее 28.12.1895г.", exception2.getMessage());

    }

    @Test
    public void shouldTrowExceptionIfDurationNotPositive() throws ValidationException {
        Film film1 = film.toBuilder().duration(0).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(film1));
        assertEquals("Продолжительность фильма должна быть больше 0", exception.getMessage());

        film = filmController.create(film);
        Film film2 = film.toBuilder().duration(0).build();

        ValidationException exception2 = assertThrows(ValidationException.class, () -> filmController.update(film2));
        assertEquals("Продолжительность фильма должна быть больше 0", exception2.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfFilmAlreadyExist() {
        film = film.toBuilder().id(1).build();

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Этот фильм уже зарегистрирован", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionIfUpdateNonexistentFilm() {
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.update(film));
        assertEquals("Такого фильма не существует", exception.getMessage());
    }
}
