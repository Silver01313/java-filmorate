package ru.yandex.practicum.filmorate.exeption;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String s) {
        super(s);
    }
}
