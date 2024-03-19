package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

@SpringBootApplication
public class FilmorateApplication {
	public static void main(String[] args) throws ValidationException {
		SpringApplication.run(FilmorateApplication.class, args);
	}

}
