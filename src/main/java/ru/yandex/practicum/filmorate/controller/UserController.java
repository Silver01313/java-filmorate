package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.AlreadyExistException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    Map<Integer, User> users = new HashMap<>();
    Integer userId = 0;

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody User user) throws ValidationException, AlreadyExistException {
        int spaceIndex = user.getLogin().indexOf(" ");
        int atIndex = user.getEmail().indexOf("@");

        if (user.getId() != null) {
            log.warn("Такой пользователь уже существует");
            throw new AlreadyExistException("Такой пользователь уже существует");
        }
        if (user.getName() == null) {
            user = user.toBuilder().name(user.getLogin()).build();
        }
        if (atIndex < 0 || user.getEmail().isBlank()) {
            log.warn("Не корректный электронный адрес");
            throw new ValidationException("Электронный адрес не может быть пустым и должен содержать @");
        }
        if (spaceIndex >= 0 || user.getLogin().isBlank()) {
            log.warn("Не корректный логин");
            throw new ValidationException("Логин не может быть пустым и не должен содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Не корректная дата рождения");
            throw new ValidationException("Не корректная дата рождения");
        }
        user = user.toBuilder().id(generateId()).build();
        users.put(user.getId(), user);
        log.debug("Вы добавили пользователя : {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) throws ValidationException {
        int spaceIndex = user.getLogin().indexOf(" ");
        int atIndex = user.getEmail().indexOf("@");

        if (!users.containsKey(user.getId())) {
            log.warn("Такого пользователя не существует");
            throw new ValidationException("Такого пользователя не существует");
        }
        if (user.getName() == null) {
            user = user.toBuilder().name(user.getLogin()).build();
        }
        if (atIndex < 0 || user.getEmail().isBlank()) {
            log.warn("Не корректный электронный адрес");
            throw new ValidationException("Электронный адрес не может быть пустым и должен содержать @");
        }
        if (spaceIndex >= 0 || user.getLogin().isBlank()) {
            log.warn("Не корректный логин");
            throw new ValidationException("Логин не может быть пустым и не должен содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Не корректная дата рождения");
            throw new ValidationException("Не корректная дата рождения");
        }
        log.debug("Вы добавили пользователя : {}", user);
        users.put(user.getId(), user);
        return user;
    }

    private Integer generateId() {
        return ++userId;
    }
}
